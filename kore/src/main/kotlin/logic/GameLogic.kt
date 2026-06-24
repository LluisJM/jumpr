package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ScoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.ScoreComponentEntry
import io.github.ayfri.kore.arguments.chatcomponents.entityComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.SelectorArgument
import io.github.ayfri.kore.arguments.types.literals.all
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.nearestEntity
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.attributes
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.fill
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.gamemode
import io.github.ayfri.kore.commands.gamerule
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.schedules
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.spawnPoint
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.commands.tp
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.gamestate.GameStateManager
import io.github.ayfri.kore.gamestate.registerGameStates
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.set

import net.benwoodworth.knbt.addNbtCompound
import registry.Settings
import registry.initializeSettings
import utils.InfiniteBorder
import utils.Timer
import utils.lockInInventoryTag
import utils.componentWithItemTag
import utils.countdown
import utils.getOrCreateTranslation
import utils.keepOnGroundTag
import utils.timerObjective

const val IDLE = "idle"
const val PRE_RUN = "pre_run"
const val RUN = "run"
const val PRE_BUILD = "pre_build"
const val BUILD = "build"

val FINISH_LINE = Blocks.LODESTONE

val gameData = scoreboard("game_data")
val settings = scoreboard("settings")

// Game data
val currentRound = literal(".round")

const val gameStart = "game/start"
const val gameStop = "game/stop"

const val finishedTag = "jumpr.finished"

const val levelStartTag = "level.start"

fun DataPack.generateGameLogic(gameTimer: Timer): GameStateManager {
    val states = registerGameStates {
        state(IDLE)
        state(PRE_RUN)
        state(RUN)
        state(PRE_BUILD)
        state(BUILD)
    }

    val startBorder = InfiniteBorder("start", Axis.Z, Relation.GREATER_THAN_OR_EQUAL_TO)

    initializeSettings()

    load {
        gameData.create()
        settings.create()
    }

    // GAME PHASES

    val startRunPhase = function("game/phase/run") {
        fun Function.fillBarrier(block: BlockArgument, filter: BlockArgument) {
            val width = 20
            val height = 30
            val distance = 2

            execute {
                asTarget(allEntities {
                    type = EntityTypes.MARKER
                    tag = levelStartTag
                })
                at(self())
                run {
                    fill(vec3(-width, -height, distance).relative, vec3(width, height, distance).relative, block, filter)
                    kill(allEntities {
                        type = EntityTypes.MARKER
                        tag = startBorder.markerTag()
                    })
                    startBorder.summonMarker(vec3(0, 0, distance).relative)
                }
            }
        }

        val actualPhase = function("${this.name}_actual") {
            scoreboard.players {
                reset(all(), roundDeaths.name)
            }

            Settings.ROUND_LENGTH.copyTo(gameTimer.ticks, timerObjective.name)

            states.transitionTo(RUN)

            // Show title
            title(allPlayers(), 0.seconds, 1.seconds, 0.2.seconds)
            title(allPlayers(), TitleLocation.TITLE, getOrCreateTranslation("run", "Run!") {
                color = Color.GREEN
            })

            // Manage tags
            tag(inGamePlayers()) {
                remove(finishedTag)
            }
            Settings.PVP.executeIf {
                gamerule(Gamerules.PVP, true)
            }

            fillBarrier(Blocks.AIR, Blocks.BARRIER)
        }

        function(startRunPhaseMusic)
        function(stopBuildPhaseMusic)
        states.transitionTo(PRE_RUN)

        // Show title "Round X"
        scoreboard.players {
            // Increase round number
            add(currentRound, gameData.name, 1)
            reset(lastFinishedPlayer, gameData.name)
        }
        title(allPlayers(), 0.seconds, 3.seconds, 0.2.seconds)
        title(allPlayers(), TitleLocation.TITLE,
            getOrCreateTranslation(
                "round.title", "Round %s", with = listOf(
                    ScoreComponent(ScoreComponentEntry(currentRound.text, gameData.name))
                )
            ) {
                color = Color.GREEN
            }
        )
        tellraw(allPlayers(),
            getOrCreateTranslation("round", "Round %s out of %s is starting soon!", with = listOf(
                ScoreComponent(ScoreComponentEntry(currentRound.text, gameData.name)),
                ScoreComponent(ScoreComponentEntry(Settings.MAX_ROUNDS.getScoreId().asString(), Settings.MAX_ROUNDS.objective.name))
            )) {
                color = Color.GREEN
            }
        )

        countdown(5, actualPhase, 5.0) { second ->
            {
                color = if (second > 3) Color.YELLOW else Color.RED
                if (second <= 1) bold = true
            }
        }

        tp(inGamePlayers(), nearestEntity {
            type = EntityTypes.MARKER
            tag = levelStartTag
        })
        fillBarrier(Blocks.BARRIER, Blocks.AIR)
        execute {
            asTarget(inGamePlayers())
            at(self())
            run {
                spawnPoint(self(), vec3().relative)
            }
        }
    }
    val startBuildPhase = function("game/phase/build") {
        val actualPhase = function("${this.name}_actual") {
            states.transitionTo(BUILD)
            // Show title "Build Phase"
            title(
                allPlayers(), TitleLocation.TITLE,
                getOrCreateTranslation("build_phase", "Build Phase") {
                    color = Color.YELLOW
                })

            execute {
                asTarget(inGamePlayers())
                run {
                    function(giveItemOptions)
                }
            }
        }

        function(startBuildPhaseMusic)
        states.transitionTo(PRE_BUILD)

        schedules.replace(actualPhase, 5.seconds)
        gamerule(Gamerules.PVP, false)

        title(allPlayers(), TitleLocation.ACTIONBAR, textComponent(""))
        gameTimer.stop()
    }
    val tryStartBuildPhase = function("game/phase/try_build") {
        val condition: ExecuteCondition.() -> Unit = {
            score(currentRound, gameData.name, Settings.MAX_ROUNDS.getScoreId(), Settings.MAX_ROUNDS.objective.name,
                Relation.GREATER_THAN_OR_EQUAL_TO)
        }

        execute {
            ifCondition(condition)
            run {
                function(gameStop)
            }
        }
        execute {
            unlessCondition(condition)
            run {
                function(startBuildPhase)
            }
        }
    }

    // GAME STARTING AND STOPING

    function(gameStart) {
        scoreboard.players {
            // Reset current round
            set(currentRound, gameData.name, -1)
        }

        gamerule(Gamerules.PVP, false)

        function(startRunPhase)
    }
    function(gameStop) {
        states.transitionTo(IDLE)

        gamerule(Gamerules.PVP, false)

        function(stopBuildPhaseMusic)

        gameTimer.stop()
    }

    // GAME LOOP

    tick("game/loop") {
        fun Function.runPhase() {
            gamemode(Gamemode.ADVENTURE, inGamePlayers())
        }

        fun Function.buildPhase() {
            gamemode(Gamemode.SURVIVAL, inGamePlayers())
            execute {
                asTarget(inGamePlayers())
                run {
                    attributes(self(), Attributes.BLOCK_BREAK_SPEED).base.set(0.0)
                }
            }
        }

        effect(inGamePlayers()) {
            giveInfinite(Effects.SATURATION, 0, true)
        }

        states.whenState(PRE_RUN) {
            runPhase()
            startBorder.ifOutside(inGamePlayers()) {
                tp(self(), vec3(0, 0, -1).relative)
            }
        }


        states.whenState(RUN) {
            runPhase()

            gameTimer.withComponent { timerComponent ->
                {
                    title(allPlayers(), TitleLocation.ACTIONBAR, timerComponent)
                }
            }

            val notFinishedPlayers = inGamePlayers {
                tag = !finishedTag
            }
            execute {
                asTarget(notFinishedPlayers)
                at(self())
                ifCondition {
                    block(vec3(0, -1, 0).relative, FINISH_LINE)
                }
                run {
                    function(playerFinish)
                    execute {
                        unlessCondition {
                            entity(notFinishedPlayers)
                        }
                        run {
                            function(tryStartBuildPhase)
                        }
                    }
                }
            }
            gameTimer.onFinish {
                execute {
                    asTarget(notFinishedPlayers)
                    run {
                        tellraw(allPlayers(), getOrCreateTranslation("finish_fail", "%s did not finish the round!",
                            listOf(entityComponent(self()) {
                                color = Color.WHITE
                            }.list[0])) {
                            color = Color.RED
                        })
                    }
                }
                function(tryStartBuildPhase)
            }
        }

        states.whenState(PRE_BUILD) {
            buildPhase()
        }

        states.whenState(BUILD) {
            buildPhase()

            execute {
                unlessCondition {
                    fun tagInInventory(tag: String) {
                        entity(inGamePlayers {
                            nbt = nbt {
                                this["Inventory"] = nbtList {
                                    addNbtCompound {
                                        this["components"] = componentWithItemTag(tag)
                                    }
                                }
                            }
                        })
                    }
                    tagInInventory(lockInInventoryTag)
                    tagInInventory(keepOnGroundTag)

                    entity(allEntities {
                        type = EntityTypes.ITEM
                        nbt = nbt {
                            this["Item"] = nbt {
                                this["components"] = componentWithItemTag(lockInInventoryTag)
                            }
                        }
                    })
                }
                run {
                    function(startRunPhase)
                }
            }
        }
    }

    return states
}

fun inGamePlayers(data: SelectorArguments.() -> Unit = {}): SelectorArgument {
    return allPlayers {
        gamemode = !Gamemode.SPECTATOR
        data()
    }
}