package registry

import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.AttributeModifierArgument
import utils.item.CustomItem
import utils.item.GamePhaseItem
import utils.item.OrbItem
import utils.item.PowerUpItem
import kotlin.collections.*

private const val goldenPickaxeDurability = 32
private const val multitoolUses = 3

interface CustomItems {
    @Suppress("unused")
    companion object {
        private val _all = mutableListOf<CustomItem>()
        val ALL: List<CustomItem> get() = _all
        private val _orbs = mutableListOf<OrbItem>()
        val ORBS: List<OrbItem> get() = _orbs

        // Build Phase Items
        // Building Type
        val BUILDING_BLOCK = register(GamePhaseItem("Building Block", "Just a building block, not much to it",
            GamePhaseItem.Type.BUILDING, Items.YELLOW_CONCRETE, 3))
        val FALLING_BUILDING_BLOCK = register(GamePhaseItem("Falling Building Block", "Just a building block, that falls!",
            GamePhaseItem.Type.BUILDING, Items.GREEN_CONCRETE_POWDER, 5))
        val SCAFFOLDING = register(GamePhaseItem("Scaffolding", "I love climbing!",
            GamePhaseItem.Type.BUILDING, Items.SCAFFOLDING, 4))
        val LADDER = register(GamePhaseItem("Ladder", "I love climbing!",
            GamePhaseItem.Type.BUILDING, Items.LADDER, 8))

        // Special Type
        val SLIME_BLOCK = register(GamePhaseItem("Slime Block", "Bouncy!",
            GamePhaseItem.Type.SPECIAL, Items.SLIME_BLOCK, 2))
        val HONEY_BLOCK = register(GamePhaseItem("Honey Block", "Sounds sticky!",
            GamePhaseItem.Type.SPECIAL, Items.HONEY_BLOCK, 2))
        val SOUL_SAND = register(GamePhaseItem("Soul Sand", "Now with 100% more souls!",
            GamePhaseItem.Type.SPECIAL, Items.SOUL_SAND, 2))
        val LAVA_BUCKET = register(GamePhaseItem("Lava Bucket", "So hot!",
            GamePhaseItem.Type.SPECIAL, Items.LAVA_BUCKET))
        val COIN = register(GamePhaseItem("Coin", "Bring this to the finish line to get extra points",
            GamePhaseItem.Type.SPECIAL, Items.GOLD_INGOT, behaviour = GamePhaseItem.Behaviour.CAN_PICK_UP))

        val CREEPER = register(GamePhaseItem("Creeper", "Kaboom!",
            GamePhaseItem.Type.SPECIAL, Items.CREEPER_SPAWN_EGG))
        val SHULKER = register(GamePhaseItem("Shulker", "We're going up, up, up...",
            GamePhaseItem.Type.SPECIAL, Items.SHULKER_SPAWN_EGG))

        val SLOWNESS_ORB = register(OrbItem("Slowness Orb", "Drop to create an area that slows down all players", 3.0, Effects.SLOWNESS, 5))
        val SPEED_ORB = register(OrbItem("Speed Orb", "Drop to create an area that speeds up all players", 3.0, Effects.SPEED, 5))

        val SPEED_POWERUP = register(PowerUpItem("Speed Power Up", "Use to give yourself momentary speed", Effects.SPEED))
        val JUMP_BOOST_POWERUP = register(PowerUpItem("Jump Boost Power Up", "Use to give yourself momentary jump boost", Effects.JUMP_BOOST))

        // Destroying Type
        val MULTITOOL = register(GamePhaseItem("Multitool", "You could break anything! Except a few things",
            GamePhaseItem.Type.DESTROYING, Items.GOLDEN_PICKAXE) {
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
                damage(goldenPickaxeDurability - multitoolUses)
            }
        )

        private fun <T: CustomItem> register(item: T): T {
            _all += item
            if (item is OrbItem) _orbs += item
            println("Registered custom item: ${item.name} (${item.dummyItem.name})")
            return item
        }
    }
}
