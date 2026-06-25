package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.scoreboard.create
import utils.Timer
import utils.timerObjective

fun DataPack.generateTimer(): Timer {
    val timer = Timer("game")

    load {
        timerObjective.create()
    }

    tick {
        execute {
            ifCondition {
                score(timer.ticks, timerObjective.name, IntRangeOrInt(IntRange(1, null)))
            }
            run {
                scoreboard.players.remove(timer.ticks, timerObjective.name, 1)
            }
        }
        timer.calculate()
    }

    return timer
}
