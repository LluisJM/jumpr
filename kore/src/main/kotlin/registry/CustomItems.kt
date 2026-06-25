package registry

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import utils.BuildPhaseItem
import utils.CustomItem
import kotlin.collections.*

interface CustomItems {
    companion object {
        private val _all = mutableListOf<CustomItem>()
        val ALL: List<CustomItem> get() = _all

        // Build Phase Items
        // Building Type
        val BUILDING_BLOCK = register(BuildPhaseItem("Building Block", "Just a building block, not much to it",
            BuildPhaseItem.Type.BUILDING, Items.YELLOW_CONCRETE, 3))
        val FALLING_BUILDING_BLOCK = register(BuildPhaseItem("Falling Building Block", "Just a building block, that falls!",
            BuildPhaseItem.Type.BUILDING, Items.GREEN_CONCRETE_POWDER, 5))
        val SCAFFOLDING = register(BuildPhaseItem("Scaffolding", "I love climbing!",
            BuildPhaseItem.Type.BUILDING, Items.SCAFFOLDING, 4))

        // Special Type
        val SLIME_BLOCK = register(BuildPhaseItem("Slime Block", "Bouncy!",
            BuildPhaseItem.Type.SPECIAL, Items.SLIME_BLOCK, 2))
        val COIN = register(BuildPhaseItem("Coin", "Bring this to the finish line to get extra points",
            BuildPhaseItem.Type.SPECIAL, Items.GOLD_INGOT, 2, true))

        // Destroying Type
        val MULTITOOL = register(BuildPhaseItem("Multitool", "You could break anything! Except a few things",
            BuildPhaseItem.Type.DESTROYING, Items.GOLDEN_PICKAXE) {
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
        )

        private fun register(item: CustomItem): CustomItem {
            _all += item
            println("Registered custom item: ${item.name} (${item.dummyItem.name})")
            return item
        }
    }
}
