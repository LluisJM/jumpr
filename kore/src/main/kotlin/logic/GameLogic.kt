package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.ScoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.ScoreComponentEntry
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.SelectorArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.attributes
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.gamemode
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.entities.getScoreEntity
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.gamestate.registerGameStates
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.scoreboard.add
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.scoreboard.setDisplayName
import io.github.ayfri.kore.scoreboard.setDisplaySlot
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase

import net.benwoodworth.knbt.addNbtCompound
import registry.initializeSettings
import utils.getOrCreateTranslation

const val IDLE = "idle"
const val RUN_COUNTDOWN = "run_countdown"
const val RUN = "run"
const val BUILD_COUNTDOWN = "build_countdown"
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
        state(RUN_COUNTDOWN)
        state(RUN)
        state(BUILD_COUNTDOWN)
        state(BUILD)
    }

    initializeSettings()

    load {
        gameData.create()
        settings.create()
    }

    // GAME PHASES

    val phaseRun = function("game/phase/run") {
        states.transitionTo(RUN)
        scoreboard.players {
            // Increase round number
            add(literal(currentRound), gameData.name, 1)
        }

        // Show title "Round X"
        title(allPlayers(), TitleLocation.TITLE,
            getOrCreateTranslation("round", "Round %s", with = listOf(
                ScoreComponent(ScoreComponentEntry(currentRound, gameData.name))
            )) {
                color = Color.GREEN
            })

        // Manage tags
        tag(inGamePlayers()) {
            remove(finishedTag)
        }
    }
    val phaseBuild = function("game/phase/build") {
        states.transitionTo(BUILD)

        // Show title "Build Phase"
        title(allPlayers(), TitleLocation.TITLE,
            getOrCreateTranslation("build_phase", "Build Phase") {
                color = Color.YELLOW
            })
    }

    // GAME STARTING AND STOPING

    function(gameStart) {
        scoreboard.players {
            // Reset current round
            set(literal(currentRound), gameData.name, -1)
        }

        function(phaseRun)
    }
    function(gameStop) {
        states.transitionTo(IDLE)
    }

    // GAME LOOP

    val playerFinish = function("player/finish") {
        val points = scoreboard("points") {
            create()
            setDisplaySlot(DisplaySlots.belowName)
            setDisplaySlot(DisplaySlots.list)
            setDisplayName(getOrCreateTranslation("points", "Points") {
                color = Color.GOLD
            })
        }
        val player = player("LluisJM") //TODO Set this to self when it's developed
        val playerPoints = player.getScoreEntity(points.name)

        fun addPoints(value: Int, source: String): Function.() -> Command {
            val msg = textComponent {
                textComponent(" +${value}p → ", color = Color.GRAY)
                getOrCreateTranslation("points.${source.snakeCase()}", source)
            }

            val block: Function.() -> Command = {
                playerPoints.add(value)
                tellraw(allPlayers(), msg)
            }
            return block
        }

        tag(self()) {
            add("finished")
        }
        tellraw(
            allPlayers(),
            getOrCreateTranslation("finish", "%s finished with %s left") { //TODO add with value
                color = Color.GREEN
            }
        )

        // Add points for finishing
        addPoints(1, "Finishing")
        // Add points for finishing 1st
        execute {
            unlessCondition {
                entity(nearestPlayer {
                    tag = finishedTag
                })
            }
            run(addPoints(1, "Finishing First"))
        }
    }
    tick("game/loop") {
        effect(inGamePlayers()) {
            giveInfinite(Effects.SATURATION, 0, true)
        }

        states.whenState(RUN) {
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
                            function(phaseBuild)
                        }
                    }
                }
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
                    function(phaseRun)
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