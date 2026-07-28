package utils.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.item.customData
import io.github.ayfri.kore.arguments.components.item.itemName
import io.github.ayfri.kore.arguments.components.item.lore
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import utils.getOrCreateTranslation
import kotlin.Int

const val customItemIdTag = "custom_item_id"

abstract class CustomItem(
    val name: String,
    val description: String,
    val nameColor: Color? = Color.WHITE,
    val dummyItem: ItemArgument,
    val defaultCount: Int = 1,
    val tags: List<String> = listOf(),
    val components: Components.() -> Unit = {}
) {
    val id: String = name.snakeCase().replace(" ", "_")
    fun asItemStack(): ItemStack = itemStack(dummyItem, defaultCount.toShort()) {
        itemName(getName())
        lore(getOrCreateTranslation("item.$id.description", description) {
            color = Color.GRAY
            italic = false
        })
        customData {
            put(customItemIdTag, id.nbt)
            tags.forEach { tag ->
                put(tag, nbt())
            }
        }
        components()
    }
    fun getName(): ChatComponents {
        val components = getOrCreateTranslation("item.$id.name", name) {
            color = nameColor
        }
        return components
    }
    context(fn: Function)
    fun give(target: EntityArgument = self()): Command = fn.give(target, asItemStack().toItemArgument(), defaultCount)
}

fun componentWithItemTag(tag: String) = nbt {
    this["minecraft:custom_data"] = nbt {
        this[tag] = nbt()
    }
}
