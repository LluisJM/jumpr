package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.functions.function
import registry.CustomItems

fun DataPack.generateDebugging() {
    function("debug/give_all_items") {
        CustomItems.ALL.forEach { customItem ->
            give(self(), customItem.toItemArgument(), customItem.count?.toInt())
        }
    }
}