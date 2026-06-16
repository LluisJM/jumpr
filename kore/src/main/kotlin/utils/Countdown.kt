package utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.schedules
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction

context(dp: DataPack)
fun Function.countdown(seconds: Int, endFunction: FunctionArgument, delay: Double = 0.0, formatting: (Int) -> PlainTextComponent.() -> Unit): Command {
    context(fn: Function)
    fun titleSecond(second: Int) {
        fn.title(allPlayers(), 0.seconds, 1.1.seconds, 0.seconds)
        fn.title(allPlayers(), TitleLocation.TITLE, textComponent(second.toString()) { formatting(second)() })
    }

    val hashCode = this@countdown.commandLines.hashCode()

    val startCountdown = dp.generatedFunction("countdown/start_$hashCode") {
        titleSecond(seconds)

        (1..<seconds).forEach { second ->
            val function = dp.generatedFunction("countdown/step_${second}_$hashCode") {
                titleSecond(second)
            }
            schedules.replace(function, (seconds - second).seconds)
        }

        schedules.replace(endFunction, seconds.seconds)
    }

    return if (delay > 0.0) schedules.replace(startCountdown, delay.seconds) else function(startCountdown)
}