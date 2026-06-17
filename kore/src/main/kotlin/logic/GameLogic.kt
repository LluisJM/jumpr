package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ScoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.ScoreComponentEntry
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.SelectorArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.attributes
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.gamemode
import io.github.ayfri.kore.commands.schedules
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.gamestate.registerGameStates
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.set

import net.benwoodworth.knbt.addNbtCompound
import registry.CustomItems
import registry.Settings
import registry.initializeSettings
import utils.countdown
import utils.getOrCreateTranslation

const val IDLE = "idle"
const val PRE_RUN = "pre_run"
const val RUN = "run"
const val PRE_BUILD = "pre_build"
const val BUILD = "build"

val FINISH_LINE = Blocks.LODESTONE

val gameData = scoreboard("game_data")
val settings = scoreboard("settings")

// Game data
const val currentRound = ".round"

// Settings
const val maxRounds = ".max_rounds"

const val gameStart = "game/start"
const val gameStop = "game/stop"

const val finishedTag = "jumpr.finished"

fun DataPack.generateGameLogic() {
    val states = registerGameStates {
        state(IDLE)
        state(PRE_RUN)
        state(RUN)
        state(PRE_BUILD)
        state(BUILD)
    }

    initializeSettings()

    load {
        gameData.create()
        settings.create()
    }

    // GAME PHASES

    val startRunPhase = function("game/phase/run") {
        val actualPhase = function("${this.name}_actual") {
            Settings.ROUND_TIME.copyTo(timerTicks, timerObjective.name)

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
        }

        states.transitionTo(PRE_RUN)

        // Show title "Round X"
        scoreboard .players {
            // Increase round number
            add(literal(currentRound), gameData.name, 1)
        }
        title(allPlayers(), 0.seconds, 3.seconds, 0.2.seconds)
        title(allPlayers(), TitleLocation.TITLE,
            getOrCreateTranslation(
                "round", "Round %s", with = listOf(
                    ScoreComponent(ScoreComponentEntry(currentRound, gameData.name))
                )
            ) {
                color = Color.GREEN
            }
        )

        countdown(5, actualPhase, 5.0) { second ->
            {
                color = if (second > 3) Color.YELLOW else Color.RED
                if (second <= 1) italic = true
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
                    function(giveItem)
                }
            }
        }

        states.transitionTo(PRE_BUILD)

        schedules.replace(actualPhase, 5.seconds)

        title(allPlayers(), TitleLocation.ACTIONBAR, textComponent(""))
        stopTimer()
    }

    // GAME STARTING AND STOPING

    function(gameStart) {
        scoreboard.players {
            // Reset current round
            set(literal(currentRound), gameData.name, -1)
        }

        function(startRunPhase)
    }
    function(gameStop) {
        states.transitionTo(IDLE)
    }

    // GAME LOOP

    tick("game/loop") {
        effect(inGamePlayers()) {
            giveInfinite(Effects.SATURATION, 0, true)
        }

        states.whenState(RUN) {
            withTimerComponent { timerComponent ->
                {
                    title(allPlayers(), TitleLocation.ACTIONBAR, timerComponent)
                }
            }()

            gamemode(Gamemode.ADVENTURE, inGamePlayers())

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
                            function(startBuildPhase)
                        }
                    }
                }
            }
            onFinishTimer {
                function(startBuildPhase)
            }
        }

        states.whenState(BUILD) {
            gamemode(Gamemode.SURVIVAL, inGamePlayers())
            execute {
                asTarget(inGamePlayers())
                run {
                    attributes(self(), Attributes.BLOCK_BREAK_SPEED).base.set(0.0)
                }
            }
            execute {
                unlessCondition {
                    val itemComponents = nbt {
                        this["minecraft:custom_data"] = nbt {
                            this["build_phase"] = nbt()
                        }
                    }

                    entity(inGamePlayers {
                        nbt = nbt {
                            this["Inventory"] = nbtList {
                                addNbtCompound {
                                    this["components"] = itemComponents
                                }
                            }
                        }
                    })
                    entity(allEntities {
                        type = EntityTypes.ITEM
                        nbt = nbt {
                            this["Item"] = nbt {
                                this["components"] = itemComponents
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

}

fun inGamePlayers(data: SelectorArguments.() -> Unit = {}): SelectorArgument {
    return allPlayers {
        gamemode = !Gamemode.SPECTATOR
        data()
    }
}