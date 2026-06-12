package registry

import io.github.ayfri.kore.arguments.chatcomponents.translatedTextComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import io.github.ayfri.kore.utils.nbt
import kotlin.collections.*

interface CustomItems {
    companion object {
        private val _all = mutableListOf<ItemStack>()
        val ALL: List<ItemStack> get() = _all

        val BUILDING_BLOCK = register("building_block", Items.YELLOW_CONCRETE, 3, buildPhase = true)
        val FALLING_BUILDING_BLOCK = register("falling_building_block", Items.GREEN_CONCRETE_POWDER, 5, buildPhase = true)
        val SCAFFOLDING = register(null, Items.SCAFFOLDING, 4, buildPhase = true)

        val MULTITOOL = register("multitool", Items.GOLDEN_PICKAXE, buildPhase = true) {
            attributeModifiers {
                modifier(
                    Attributes.BLOCK_BREAK_SPEED,
                    amount = 1.0,
                    id = AttributeModifierArgument("allow_breaking"),
                    operation = AttributeModifierOperation.ADD_VALUE,
                    slot = EquipmentSlot.MAINHAND
                )
            }
            tooltipDisplay(false, ItemComponentTypes.ATTRIBUTE_MODIFIERS)
        }

        private fun register(id: String?, dummyItem: ItemArgument = Items.ECHO_SHARD, count: Short = 1, buildPhase: Boolean = false,
                             extraTags: Array<String> = arrayOf(), components: Components.() -> Unit = {}): ItemStack {
            val itemStack = itemStack(dummyItem, count) {
                if (id != null) {
                    itemName(translatedTextComponent("jumpr.item.$id.name"))
                }
                lore(translatedTextComponent("jumpr.item.$id.description") {
                    color = Color.GRAY
                    italic = false
                })
                customData {
                    if (buildPhase) {
                        put("build_phase", nbt())
                    }
                    put("custom_item_id", id?.nbt ?: dummyItem.name.lowercase().nbt)
                    for (tag in extraTags) {
                        put(tag, nbt())
                    }
                }
                components()
            }
            _all += itemStack
            println("Registered custom item: $id (${dummyItem.name})")
            return itemStack
        }
    }
}
