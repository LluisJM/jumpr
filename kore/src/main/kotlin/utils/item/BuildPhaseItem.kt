package utils.item

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

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

    context(fn: Function)
    fun asAndAtItem(block: Function.() -> Unit) = fn.execute {
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
