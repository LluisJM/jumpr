package utils.setting

import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase

class BooleanSetting(
    name: String,
    defaultValue: Boolean = false,
    id: String = name.snakeCase().replace(" ", "_")
): AbstractSetting(
    name,
    defaultValue.compareTo(false),
    id
) {
    val interaction = createInteractionFace()

    context(fn: Function)
    fun toggle(): Command {
        fn.scoreboard.players.add(getScoreId(), objective.name, 1)
        return fn.execute {
            ifCondition {
                score(getScoreId(), objective.name, IntRange(2, null).asRangeOrInt())
            }
            run {
                scoreboard.players.set(getScoreId(), objective.name, 0)
            }
        }
    }

    context(fn: Function)
    override fun summonButton(pos: Vec3, axis: Axis) {
        super.summonButton(pos, axis)

        fn.summonInteractionFace(pos, interaction)
        fn.summon(EntityTypes.TEXT_DISPLAY, pos) {
            this["Tags"] = getEntityTags(true)
            this["text"] = "[x]"
            this["background"] = 0
            if (axis == Axis.Z) this["Rotation"] = nbtListOf(90f, 0f)
        }
    }


    context(fn: Function)
    fun executeIf(value: Boolean = true, block: Function.() -> Unit): Command {
        val range = IntRange(if (value) 1 else null, if (value) null else 0).asRangeOrInt()
        return fn.execute {
            ifCondition {
                score(getScoreId(), objective.name, range)
            }
            run(block)
        }
    }

    context(fn: Function)
    override fun updateDisplay() {
        fn.execute {
            asTarget(entity(EntityTypes.TEXT_DISPLAY) {
                tag = displayTag
            })
            run {
                executeIf {
                    data(self())["text"] = "[x]"
                }
                executeIf(false) {
                    data(self())["text"] = "[ ]"
                }
            }
        }
    }

    context(fn: Function)
    override fun tick() {
        interaction.onInteract {
            toggle()
        }
    }
}
