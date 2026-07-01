package game

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.DisplaySlots
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.TranslatedTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.entityComponent
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.customData
import io.github.ayfri.kore.arguments.components.itemPredicate
import io.github.ayfri.kore.arguments.components.partial
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.clear
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.scoreboard.setDisplayName
import io.github.ayfri.kore.scoreboard.setDisplaySlot
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import registry.CustomItems
import utils.Timer
import utils.customItemIdTag
import utils.getOrCreateTranslation

const val playerFinish = "player/finish"
val lastFinishedPlayer = literal(".last_finished_player")
val roundDeaths = scoreboard("round_deaths")

const val finishingFirstPoints = 5
const val coinPoints = 5
const val noDeathsPoints = 3

fun DataPack.generatePointLogic(gameTimer: Timer) {
    load {
        roundDeaths.create(ScoreboardCriteria.DEATH_COUNT)
    }

    function(playerFinish) {
        val points = scoreboard("points") {
            create()
            setDisplaySlot(DisplaySlots.belowName)
            setDisplaySlot(DisplaySlots.list)
            setDisplayName(getOrCreateTranslation("points", "Points") {
                color = Color.GOLD
            })
        }

        fun getPointMessage(points: ChatComponents, last: ChatComponent) = ChatComponents(
            text("  ") {
                color = Color.GRAY
            },
            getOrCreateTranslation("points.finish", "+%s points") {
                with = listOf(points)
            }.list[0],
            text(" → "),
            last
        )

        fun getSourceText(source: String, block: TranslatedTextComponent.() -> Unit = {}) = getOrCreateTranslation("points.source.${source.snakeCase()}", source, block = block)

        context(fn: Function)
        fun addPoints(value: Int, source: String): Function.() -> Command {
            val msg = getPointMessage(
                textComponent("$value"),
                getSourceText(source).first()
            )

            val block: Function.() -> Command = {
                fn.scoreboard.players.add(self(), points.name, value)
                fn.tellraw(allPlayers(), msg)
            }
            return block
        }

        scoreboard.players.add(lastFinishedPlayer, gameData.name, 1)

        gameTimer.withComponent { timerComponent ->
            {
                tellraw(
                    allPlayers(),
                    getOrCreateTranslation(
                        "finish", "%s finished with %s left",
                        with = listOf(
                            entityComponent(self()) {
                                color = Color.WHITE
                            },
                            timerComponent
                        )
                    ) {
                        color = Color.GREEN
                    }
                )
            }
        }

        // Add points for finishing
        addPoints(1, "Finishing")()

        // Add points for finishing on the top 5
        listOf("Finishing First", "Finishing Second", "Finishing Third", "Finishing Fourth", "Finishing Fifth").onEachIndexed { i, string ->
            fun givePositionPoints(position: Int, points: Int, string: String) {
                execute {
                    ifCondition {
                        score(lastFinishedPlayer, gameData.name, IntRangeOrInt(null, position))
                    }
                    run {
                        addPoints(points, string)()
                    }
                }
            }
            givePositionPoints(i + 1, finishingFirstPoints - i, string)
        }

        execute {
            unlessCondition {
                score(self(), roundDeaths.name, IntRangeOrInt(IntRange(1, null)))
            }
            run {
                addPoints(noDeathsPoints, "No Deaths")()
            }
        }

        val totalCoins = literal("#temp1")
        val totalCoinPoints = literal("#temp2")

        scoreboard {
            objective(points.name) {
                reset(totalCoins)
                reset(totalCoinPoints)
            }
        }

        // Deal with coins
        execute {
            storeResult {
                score(totalCoins, points.name)
            }
            run {
                clear(self(), itemPredicate {
                    customData {
                        this[customItemIdTag] = CustomItems.COIN.id
                    }
                    partial(ItemComponentTypes.CUSTOM_DATA)
                })
            }
        }
        scoreboard.objective(points.name) {
            set(totalCoinPoints, coinPoints)
            operation(totalCoinPoints, Operation.MULTIPLY, totalCoins, points.name)
            operation(self(), Operation.ADD, totalCoinPoints, points.name)
        }
        execute {
            ifCondition {
                score(totalCoins, points.name, IntRangeOrInt(IntRange(1, null)))
            }
            run {
                tellraw(allPlayers(), getPointMessage(
                    scoreComponent(points.name, totalCoinPoints),
                    getSourceText("Coin") {
                        extra = ChatComponents(
                            text(" x"),
                            scoreComponent(points.name, totalCoins).first()
                        )
                    }.first()
                ))
            }
        }

        // Add tag for finishing
        tag(self()) {
            add(finishedTag)
        }
    }
}