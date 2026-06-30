package dev

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.functions.function
import registry.CustomItems
import utils.countdown

fun DataPack.generateDebugging() {
    function("debug/give_all_items") {
        CustomItems.ALL.forEach { item ->
            item.give()
        }
    }

    function("debug/countdown") {
        val endFunction = function("debug/countdown_end") {
            say("Countdown ended")
        }
        countdown(5, endFunction) { second ->
            {
                color = if (second <= 3) Color.RED else Color.YELLOW
            }
        }
    }
}