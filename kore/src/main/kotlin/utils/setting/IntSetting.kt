package utils.setting

import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
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

open class IntSetting(
    name: String,
    val minValue: Int,
    val maxValue: Int,
    defaultValue: Int = minValue,
    id: String = name.snakeCase().replace(" ", "_")
): AbstractSetting(
    name,
    defaultValue,
    id
) {
    open val buttonXOffset = 0.5

    val removeInteraction = createInteractionFace("remove_button")
    val addInteraction = createInteractionFace("add_button")

    context(fn: Function)
    fun add(step: Int = 1): Command {
        fn.scoreboard.players {
            if (step < 0) {
                remove(getScoreId(), objective.name, -step)
            } else {
                add(getScoreId(), objective.name, step)
            }
        }
        executeIfScoreRange(this, null, minValue - 1) {
            scoreboard.players.set(getScoreId(), objective.name, minValue)
        }
        return executeIfScoreRange(this, maxValue + 1, null) {
            scoreboard.players.set(getScoreId(), objective.name, maxValue)
        }
    }

    context(fn: Function)
    override fun summonButton(pos: Vec3, axis: Axis) {
        super.summonButton(pos, axis)
        val removeButtonPosition = vec3WithAxis(-buttonXOffset, axis)
        val addButtonPosition = vec3WithAxis(buttonXOffset, axis)

        fn.summonInteractionFace(pos.plus(removeButtonPosition), removeInteraction)
        fn.summonInteractionFace(pos.plus(addButtonPosition), addInteraction)
        fn.summon(EntityTypes.TEXT_DISPLAY, pos) {
            this["Tags"] = getEntityTags(true)
            this["text"] = "--"
            this["background"] = 0
            if (axis == Axis.Z) this["Rotation"] = nbtListOf(90f, 0f)
        }
        fn.summon(EntityTypes.TEXT_DISPLAY, pos.plus(removeButtonPosition)) {
            this["Tags"] = getEntityTags()
            this["text"] = "[-]"
            this["background"] = 0
            if (axis == Axis.Z) this["Rotation"] = nbtListOf(90f, 0f)
        }
        fn.summon(EntityTypes.TEXT_DISPLAY, pos.plus(addButtonPosition)) {
            this["Tags"] = getEntityTags()
            this["text"] = "[+]"
            this["background"] = 0
            if (axis == Axis.Z) this["Rotation"] = nbtListOf(90f, 0f)
        }
    }

    context(fn: Function)
    override fun updateDisplay() {
        fn.execute {
            asTarget(entity(EntityTypes.TEXT_DISPLAY) {
                tag = displayTag
            })
            run {
                data(self())["text"] = scoreComponent(objective.name, getScoreId())
            }
        }
    }

    context(fn: Function)
    override fun tick() {
        removeInteraction.onInteract {
            add(-1)
        }
        addInteraction.onInteract {
            add(1)
        }
    }
}

private fun vec3WithAxis(offset: Double, axis: Axis) = vec3(if (axis == Axis.X) -offset else 0, 0, if (axis == Axis.Z) -offset else 0)
