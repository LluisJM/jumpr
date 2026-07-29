package utils.setting

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.SelectorArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.add
import utils.Interaction
import utils.getOrCreateTranslation
import kotlin.collections.forEach

const val TEXT_HEIGHT = 1.0 / 16.0 * 4.0
const val displayTag = "jumpr.setting.display"

val settings = scoreboard("settings")

abstract class AbstractSetting (
    val name: String,
    val defaultValue: Int,
    val id: String = name.snakeCase().replace(" ", "_")
) {
    open val objective = settings

    fun getTranslationKey() = "setting.$id"

    open fun getScoreId() = literal(".$id")

    context(fn: Function)
    fun reset(): Command {
        return fn.scoreboard.players.set(getScoreId(), objective.name, defaultValue)
    }

    context(fn: Function)
    fun copyTo(target: ScoreHolderArgument, objective: String): Command {
        return fn.scoreboard.players.operation(target, objective, Operation.SET, getScoreId(), this.objective.name)
    }

    context(fn: Function)
    open fun summonButton(pos: Vec3, axis: Axis) {
        if (axis == Axis.Y) throw IllegalArgumentException("You cannot align settings button to vertical axis")

        fn.summon(EntityTypes.TEXT_DISPLAY, pos.plus(vec3(0, TEXT_HEIGHT, 0))) {
            this["Tags"] = getEntityTags()
            this["text"] = getOrCreateTranslation(getTranslationKey(), name) {
                color = Color.BLUE
            }.toNbtTag()
            this["background"] = 0
            if (axis == Axis.Z) this["Rotation"] = nbtListOf(90f, 0f)
        }
    }

    context(fn: Function)
    abstract fun updateDisplay()

    fun entity(entityType: EntityTypeArgument? = null, data: SelectorArguments.() -> Unit = {}): SelectorArgument {
        return allEntities {
            type = entityType
            tag = entityTag()
            data()
        }
    }

    fun entityTag(): String = "jumpr.setting.$id"

    fun getEntityTags(display: Boolean = false, vararg extraElements: String): NbtList<NbtString> = nbtList<NbtString> {
        add("jumpr.setting")
        add(entityTag())
        if (display) add(displayTag)
        extraElements.forEach {
            add(it)
        }
    }

    context(fn: Function)
    abstract fun tick()
}

context(setting: AbstractSetting)
fun createInteractionFace(name: String = "button", width: Double = 0.4): Interaction {
    val interaction = Interaction(
        setting.getEntityTags(false, "jumpr.setting.${setting.id}.$name"),
        TEXT_HEIGHT,
        width
    )

    return interaction
}

fun Function.summonInteractionFace(pos: Vec3, interaction: Interaction): Command {
    return interaction.summon(pos.plus(vec3(interaction.width / 2, 0, 0)))
}

context(fn: Function)
fun executeIfScoreRange(setting: AbstractSetting, start: Int?, end: Int?, block: Function.() -> Unit): Command {
    return fn.execute {
        ifCondition {
            score(setting.getScoreId(), setting.objective.name, IntRange(start, end).asRangeOrInt())
        }
        run(block)
    }
}
