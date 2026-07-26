package utils

import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtString

class Interaction(
    val tags: NbtList<NbtString>,
    val height: Double = 1.0,
    val width: Double = 1.0,
    val nbt: NbtCompoundBuilder.() -> Unit = {}
) {
    constructor(name: String, height: Double = 1.0, width: Double = 1.0, nbt: NbtCompoundBuilder.() -> Unit = {}) :
            this(nbtListOf(name), height, width, nbt)

    context(fn: Function)
    fun summon(pos: Vec3 = vec3()): Command {
        return fn.summon(EntityTypes.INTERACTION, pos) {
            nbt()
            this["Tags"] = tags
            this["height"] = height
            this["width"] = width
        }
    }

    context(fn: Function)
    fun onInteract(block: Function.() -> Unit): Command {
        return fn.execute {
            asTarget(allEntities {
                for (tag in tags) {
                    this.tag = tag.value
                }
            })
            ifCondition {
                data(self(), "interaction")
            }
            run {
                block()
                data(self()).remove("interaction")
            }
        }
    }
}