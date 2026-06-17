package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.commands.loot
import io.github.ayfri.kore.features.loottables.entries
import io.github.ayfri.kore.features.loottables.entries.item
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.functions.function
import registry.CustomItems

const val giveItem = "items/give"

fun DataPack.generateItemLogic() {
    val allItems = lootTable("round_items/all") {
        pool(constant(1f)) {
            entries {
                CustomItems.ALL.forEach { item ->
                    item(item.toItemArgument())
                }
            }
        }
    }

    function(giveItem) {
        give(self(), CustomItems.SCAFFOLDING.toItemArgument())
    }
}