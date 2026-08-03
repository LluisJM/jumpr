package registry

import io.github.ayfri.kore.arguments.colors.color
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
        private val all = mutableListOf<CustomItem>()
        val ALL: List<CustomItem> get() = all
        private val gameItems = mutableListOf<GamePhaseItem>()
        val GAME_ITEMS: List<GamePhaseItem> get() = gameItems
        private val orbs = mutableListOf<OrbItem>()
        val ORBS: List<OrbItem> get() = orbs

        val buildingPool = mutableListOf<GamePhaseItem>()
        val BUILDING_POOL: List<GamePhaseItem> get() = buildingPool
        val specialPool = mutableListOf<GamePhaseItem>()
        val SPECIAL_POOL: List<GamePhaseItem> get() = specialPool
        val destroyingPool = mutableListOf<GamePhaseItem>()
        val DESTROYING_POOL: List<GamePhaseItem> get() = destroyingPool

        val runPhasePool = mutableListOf<GamePhaseItem>()
        val RUN_PHASE_POOL: List<GamePhaseItem> get() = runPhasePool

        // Build Phase Items
        // Building Type
        val BUILDING_BLOCK = register(GamePhaseItem("Building Block", "Just a building block, not much to it",
            Items.YELLOW_CONCRETE, 3), buildingPool)
        val FALLING_BUILDING_BLOCK = register(GamePhaseItem("Falling Building Block", "Just a building block, that falls!",
            Items.GREEN_CONCRETE_POWDER, 5), buildingPool)
        val SCAFFOLDING = register(GamePhaseItem("Scaffolding", "I love climbing!",
            Items.SCAFFOLDING, 4), buildingPool)
        val LADDER = register(GamePhaseItem("Ladder", "I love climbing!",
            Items.LADDER, 8), buildingPool)

        // Special Type
        val SLIME_BLOCK = register(GamePhaseItem("Slime Block", "Bouncy!",
            Items.SLIME_BLOCK, 2), specialPool)
        val HONEY_BLOCK = register(GamePhaseItem("Honey Block", "Sounds sticky!",
            Items.HONEY_BLOCK, 2), specialPool)
        val SOUL_SAND = register(GamePhaseItem("Soul Sand", "Now with 100% more souls!",
            Items.SOUL_SAND, 2), specialPool)
        val LAVA_BUCKET = register(GamePhaseItem("Lava Bucket", "So hot!",
            Items.LAVA_BUCKET), specialPool)
        val COIN = register(GamePhaseItem("Coin", "Bring this to the finish line to get extra points",
            Items.GOLD_INGOT, behaviour = GamePhaseItem.Behaviour.CAN_PICK_UP), specialPool)

        val CREEPER = register(GamePhaseItem("Creeper", "Kaboom!",
            Items.CREEPER_SPAWN_EGG), specialPool)
        val SHULKER = register(GamePhaseItem("Shulker", "We're going up, up, up...",
            Items.SHULKER_SPAWN_EGG), specialPool)

        val SLOWNESS_ORB = register(OrbItem("Slowness Orb", "Drop to create an area that slows down all players", 3.0, Effects.SLOWNESS, 5, color("34364e")), specialPool)
        val SPEED_ORB = register(OrbItem("Speed Orb", "Drop to create an area that speeds up all players", 3.0, Effects.SPEED, 5, color("54dce4")), specialPool)

        // Destroying Type
        val MULTITOOL = register(GamePhaseItem("Multitool", "You could break anything! Except a few things",
            Items.GOLDEN_PICKAXE) {
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
            },
            destroyingPool
        )

        // Run phase
        val SPEED_POWERUP = register(PowerUpItem("Speed Power Up", "Use to give yourself momentary speed", Effects.SPEED), runPhasePool)
        val JUMP_BOOST_POWERUP = register(PowerUpItem("Jump Boost Power Up", "Use to give yourself momentary jump boost", Effects.JUMP_BOOST), runPhasePool)
        val LEVITATION_POWERUP = register(PowerUpItem("Levitation Power Up", "Use to give yourself momentary levitation", Effects.LEVITATION), runPhasePool)

        private fun <T: U, U: CustomItem> register(item: T, vararg pools: MutableList<U>): T {
            all += item
            println("Registered custom item: ${item.name} (${item.dummyItem.name}) of class ${item.javaClass}")
            if (item is GamePhaseItem) {
                gameItems += item
                if (item is OrbItem) orbs += item
                pools.forEach { pool ->
                    pool += item
                }
            }
            return item
        }
    }
}
