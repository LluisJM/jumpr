package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard

val timerObjective = scoreboard("timer")
val timerVar = literal(".timer")

fun DataPack.generateTimer() {
    load {
        timerObjective.create()
    }

    tick("timer/tick") {
        execute {
            ifCondition {
                score(timerVar, timerObjective.name, IntRangeOrInt(IntRange(null, -1)))
            }
            run {
                scoreboard.players.remove(timerVar, timerObjective.name, 1)
            }
        }
    }
}

fun Function.onFinishTimer(block: Function.() -> Command) {
    execute {
        ifCondition {
            score(timerVar, timerObjective.name, IntRangeOrInt(null, 0))
        }
        run(block)
    }
}