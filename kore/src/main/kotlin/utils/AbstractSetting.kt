package utils

import io.github.ayfri.kore.arguments.chatcomponents.scoreComponent
import io.github.ayfri.kore.arguments.chatcomponents.translatedTextComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.arguments.types.literals.LiteralArgument
import io.github.ayfri.kore.arguments.types.literals.SelectorArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbtList
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.add
import kotlin.collections.forEach

const val TEXT_HEIGHT = 1.0 / 16.0 * 4.0

val settings = scoreboard("settings")

abstract class AbstractSetting (
    val name: String,
    val defaultValue: Int,
    val id: String = name.snakeCase().replace(" ", "_")
) {
    fun getTranslationKey(): String {
        return "setting.$id"
    }

    fun getScoreId(): LiteralArgument {
        return literal(".$id")
    }

    context(fn: Function)
    fun reset(): Command {
        return fn.scoreboard.players.set(getScoreId(), settings.name, defaultValue)
    }

    context(fn: Function)
    fun copyTo(target: ScoreHolderArgument, objective: String): Command {
        return fn.scoreboard.players.operation(target, objective, Operation.SET, getScoreId(), settings.name)
    }

    context(fn: Function)
    open fun createInteraction(pos: Vec3) {
        fn.summon(EntityTypes.TEXT_DISPLAY, pos.plus(vec3(0, TEXT_HEIGHT, 0))) {
            this["Tags"] = getEntityTags()
            this["text"] = getOrCreateTranslation(getTranslationKey(), name) {
                color = Color.GRAY
            }.toNbtTag()
        }
    }

    context(fn: Function)
    abstract fun onDisplayUpdate(): Command

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
        if (display) add("jumpr.setting.display")
        extraElements.forEach {
            add(it)
        }
    }

    context(fn: Function)
    abstract fun tick()
}

class BooleanSetting(
    name: String,
    defaultValue: Boolean = false,
    id: String = name.snakeCase().replace(" ", "_")
) : AbstractSetting(
    name,
    defaultValue.compareTo(false),
    id
) {
    val interaction = createInteractionFace()

    context(fn: Function)
    fun toggle(): Command {
        fn.scoreboard.players.add(getScoreId(), settings.name, 1)
        return fn.execute {
            ifCondition {
                score(getScoreId(), settings.name, IntRange(2, null).asRangeOrInt())
            }
            run {
                scoreboard.players.set(getScoreId(), settings.name, 0)
            }
        }
    }

    context(fn: Function)
    override fun createInteraction(pos: Vec3) {
        super.createInteraction(pos)

        fn.summonInteractionFace(pos, interaction)
        fn.summon(EntityTypes.TEXT_DISPLAY, pos) {
            this["Tags"] = getEntityTags(true)
            this["text"] = "[x]"
        }
    }


    context(fn: Function)
    fun executeIf(value: Boolean = true, block: Function.() -> Command): Command {
        val range = IntRange(if (value) 1 else null, if (value) null else 0).asRangeOrInt()
        return fn.execute {
            ifCondition {
                score(getScoreId(), settings.name, range)
            }
            run(block)
        }
    }

    context(fn: Function)
    override fun onDisplayUpdate(): Command {
        return fn.execute {
            asTarget(entity(EntityTypes.TEXT_DISPLAY) {
                tag = "jumpr.setting.display"
            })
            run {
                executeIf {
                    data(self()).set("text", "[x]")
                }
                executeIf(false) {
                    data(self()).set("text", "[ ]")
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

class IntSetting(
    name: String,
    val minValue: Int,
    val maxValue: Int,
    defaultValue: Int = minValue,
    id: String = name.snakeCase().replace(" ", "_")
) : AbstractSetting(
    name,
    defaultValue,
    id
) {
    val removeInteraction = createInteractionFace("remove_button")
    val addInteraction = createInteractionFace("add_button")

    context(fn: Function)
    fun add(step: Int = 1): Command {
        fn.scoreboard.players {
            if (step < 0) {
                remove(getScoreId(), settings.name, -step)
            } else {
                add(getScoreId(), settings.name, step)
            }
        }
        executeIfScoreRange(this, null, minValue - 1) {
            scoreboard.players.set(getScoreId(), settings.name, minValue)
        }
        return executeIfScoreRange(this, maxValue + 1, null) {
            scoreboard.players.set(getScoreId(), settings.name, maxValue)
        }
    }

    context(fn: Function)
    override fun createInteraction(pos: Vec3) {
        val buttonXOffset = 0.5

        super.createInteraction(pos)
        fn.summonInteractionFace(pos.plus(vec3(-buttonXOffset, 0, 0)), removeInteraction)
        fn.summonInteractionFace(pos.plus(vec3(buttonXOffset, 0, 0)), addInteraction)
        fn.summon(EntityTypes.TEXT_DISPLAY, pos) {
            this["Tags"] = getEntityTags(true)
            this["text"] = "12"
        }
        fn.summon(EntityTypes.TEXT_DISPLAY, pos.plus(vec3(-buttonXOffset, 0.0, 0.0))) {
            this["Tags"] = getEntityTags()
            this["text"] = "[-]"
        }
        fn.summon(EntityTypes.TEXT_DISPLAY, pos.plus(vec3(buttonXOffset, 0.0, 0.0))) {
            this["Tags"] = getEntityTags()
            this["text"] = "[+]"
        }
    }

    context(fn: Function)
    override fun onDisplayUpdate(): Command {
        return fn.execute {
            asTarget(entity(EntityTypes.TEXT_DISPLAY) {
                tag = "jumpr.setting.display"
            })
            run {
                data(self()).set("text", scoreComponent(settings.name, getScoreId()))
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

context(setting: AbstractSetting)
fun createInteractionFace(name: String = "button", width: Double = 0.4): Interaction {
    val interaction = Interaction(setting.getEntityTags(false, "jumpr.setting.${setting.id}.$name"), TEXT_HEIGHT, width)

    return interaction
}

context(setting: AbstractSetting)
fun Function.summonInteractionFace(pos: Vec3, interaction: Interaction): Command {
    return interaction.summon(pos.plus(vec3(0, 0, -(interaction.width / 2))))
}

context(fn: Function)
fun executeIfScoreRange(setting: AbstractSetting, start: Int?, end: Int?, block: Function.() -> Command): Command {
    return fn.execute {
        ifCondition {
            score(setting.getScoreId(), settings.name, IntRange(start, end).asRangeOrInt())
        }
        run(block)
    }
}