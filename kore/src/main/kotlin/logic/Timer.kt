package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard

val timerObjective = scoreboard("timer")
val timerTicks = literal(".ticks")
private val minutes = literal(".min")
private val seconds = literal(".sec")
private val deciseconds = literal(".ds")

fun DataPack.generateTimer() {
    load {
        timerObjective.create()
    }

    tick("timer/tick") {
        execute {
            ifCondition {
                score(timerTicks, timerObjective.name, IntRangeOrInt(IntRange(1, null)))
            }
            run {
                scoreboard.players.remove(timerTicks, timerObjective.name, 1)
            }
        }

        scoreboard.players {
            val accumulatedTicks = literal(".accumulated_ticks")

            fun setTimerNumber(holder: ScoreHolderArgument, divisor: Int): () -> Command {
                val temp0 = literal(".temp0")
                val temp1 = literal(".temp1")


                val unit = {
                    set(temp0, timerObjective.name, divisor)
                    operation(holder, timerObjective.name, Operation.SET, timerTicks, timerObjective.name)

                    operation(holder, timerObjective.name, Operation.REMOVE, accumulatedTicks, timerObjective.name)
                    operation(holder, timerObjective.name, Operation.DIVIDE, temp0, timerObjective.name)

                    operation(temp1, timerObjective.name, Operation.SET, holder, timerObjective.name)
                    operation(temp1, timerObjective.name, Operation.MULTIPLY, temp0, timerObjective.name)
                    operation(accumulatedTicks, timerObjective.name, Operation.ADD, temp1, timerObjective.name)
                }

                return unit
            }

            reset(accumulatedTicks, timerObjective.name)
            setTimerNumber(minutes, 60 * 20)()
            setTimerNumber(seconds, 20)()
            setTimerNumber(deciseconds, 2)()
        }
    }
}

fun Function.onFinishTimer(block: Function.() -> Command) {
    execute {
        ifCondition {
            score(timerTicks, timerObjective.name, IntRangeOrInt(null, 0))
        }
        run(block)
    }
}

fun Function.startTimer(seconds: Int) {
    scoreboard.players.set(timerTicks, timerObjective.name, seconds * 20)
}

fun Function.stopTimer() {
    scoreboard.players.set(timerTicks, timerObjective.name, 0)
}

fun Function.withTimerComponent(action: (component: ChatComponents) -> Function.() -> Command): Function.() -> Unit {
    return {
        arrayOf(true, false).forEach { doubleDigits ->
            val range = if (doubleDigits) IntRange(10, null) else IntRange(null, 9)
            val prefix = if (doubleDigits) "" else "0"

            execute {
                ifCondition {
                    score(seconds, timerObjective.name, IntRangeOrInt(range))
                }
                run(action(ChatComponents(
                    scoreComponent(timerObjective.name, minutes).list[0],
                    text(":$prefix"),
                    scoreComponent(timerObjective.name, seconds).list[0],
                    text("."),
                    scoreComponent(timerObjective.name, deciseconds).list[0]))
                )
            }
        }
    }
}