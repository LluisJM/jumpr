package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.entityComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.scoreboard.setDisplayName
import io.github.ayfri.kore.scoreboard.setDisplaySlot
import io.github.ayfri.kore.utils.snakeCase
import utils.Timer
import utils.getOrCreateTranslation

const val playerFinish = "player/finish"

fun DataPack.generatePointLogic(gameTimer: Timer) {
    function(playerFinish) {
        val points = scoreboard("points") {
            create()
            setDisplaySlot(DisplaySlots.belowName)
            setDisplaySlot(DisplaySlots.list)
            setDisplayName(getOrCreateTranslation("points", "Points") {
                color = Color.GOLD
            })
        }

        context(fn: io.github.ayfri.kore.functions.Function)
        fun addPoints(value: Int, source: String): io.github.ayfri.kore.functions.Function.() -> Command {
            val msg = ChatComponents(
                textComponent(" +${value}p → ", color = Color.GRAY).list[0],
                getOrCreateTranslation("points.${source.snakeCase()}", source).list[0]
            )

            val block: Function.() -> Command = {
                fn.scoreboard.players.add(self(), points.name, value)
                fn.tellraw(allPlayers(), msg)
            }
            return block
        }

        gameTimer.withComponent { timerComponent ->
            {
                tellraw(
                    allPlayers(),
                    getOrCreateTranslation(
                        "finish", "%s finished with %s left",
                        with = listOf(
                            entityComponent(self()) {
                                color = Color.WHITE
                            }.list[0],
                            timerComponent.list[0] // TODO: Add whole component
                        )
                    ) {
                        color = Color.GREEN
                    }
                )
            }
        }()

        // Add points for finishing
        addPoints(1, "Finishing")()

        // Add points for finishing 1st
        execute {
            unlessCondition {
                entity(nearestPlayer {
                    tag = finishedTag
                })
            }
            run {
                addPoints(1, "Finishing First")()
            }
        }

        // Add tag for finishing
        tag(self()) {
            add(finishedTag)
        }
    }
}