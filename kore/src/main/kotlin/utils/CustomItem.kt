package utils

import game.inGamePlayers
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.item.customData
import io.github.ayfri.kore.arguments.components.item.itemName
import io.github.ayfri.kore.arguments.components.item.lore
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
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

open class BuildPhaseItem(
    name: String,
    description: String,
    val type: Type,
    dummyItem: ItemArgument,
    defaultCount: Int = 1,
    behaviour: Behaviour = Behaviour.LOCK_IN_INVENTORY,
    tags: List<String> = listOf(),
    components: Components.() -> Unit = {}
): CustomItem(
    name,
    description,
    type.color,
    dummyItem,
    defaultCount,
    tags.plus(behaviour.tag),
    components
) {
    enum class Type(val color: Color) {
        BUILDING(Color.YELLOW),
        DESTROYING(Color.RED),
        SPECIAL(Color.BLUE)
    }

    enum class Behaviour(val tag: String) {
        LOCK_IN_INVENTORY("lock_in_inventory"),
        KEEP_ON_GROUND("keep_on_ground"),
        CAN_PICK_UP("can_pick_up")
    }
}

const val orbTag = "orb"

class OrbItem(
    name: String,
    description: String,
    val radius: Double,
    dummyItem: ItemArgument = Items.GLASS_BOTTLE,
    defaultCount: Int = 1,
    tags: List<String> = listOf(),
    components: Components.() -> Unit = {},
    val effect: Function.() -> Unit
): BuildPhaseItem(
    name,
    description,
    Type.SPECIAL,
    dummyItem,
    defaultCount,
    Behaviour.KEEP_ON_GROUND,
    tags.plus(orbTag),
    components
) {
    constructor(
        name: String,
        description: String,
        radius: Double,
        effect: MobEffectArgument,
        effectAmplifier: Int,
        dummyItem: ItemArgument = Items.GLASS_BOTTLE,
        defaultCount: Int = 1,
        effectDuration: Int = 1,
        tags: List<String> = listOf(),
        components: Components.() -> Unit = {}
    ) : this(
        name,
        description,
        dummyItem,
        defaultCount,
        tags,
        components,
        {
            effect(inGamePlayers {
                distance = range(0.0, radius)
            }) {
                give(effect, effectDuration, effectAmplifier)
            }
        }
    )

    context(fn: Function)
    fun applyEffect() = asAndAtOrb(effect)

    context(fn: Function)
    fun destroy() = asAndAtOrb {
        playSound(SoundEvents.Entity.Item.BREAK, PlaySoundMixer.MASTER, allPlayers(), vec3().relative)
    }

    context(fn: Function)
    private fun asAndAtOrb(block: Function.() -> Unit) = fn.execute {
        asTarget(allEntities {
            type = EntityTypes.ITEM
            nbt = nbt {
                this["Item"] = nbt {
                    this["components"] = nbt {
                        this["minecraft:custom_data"] = nbt {
                            this[customItemIdTag] = id
                        }
                    }
                }
            }
        })
        at(self())
        run(block)
    }
}