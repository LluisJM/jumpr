package utils.setting

import io.github.ayfri.kore.arguments.types.literals.LiteralArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.snakeCase
import utils.Timer
import utils.timerObjective

class TimeSetting(
    name: String,
    minSeconds: Int,
    maxSeconds: Int,
    defaultSeconds: Int,
    id: String = name.snakeCase().replace(" ", "_"),
    val timer: Timer = Timer(id)
): IntSetting(
    name,
    minSeconds * 20,
    maxSeconds * 20,
    defaultSeconds * 20,
    id
) {
    override val buttonXOffset = 0.7
    override val objective = timerObjective

    override fun getScoreId(): LiteralArgument = literal(".$id.ticks")

    val step = 5 * 20

    context(fn: Function)
    override fun updateDisplay() {
        timer.calculate()
        timer.withComponent { components ->
            {
                execute {
                    asTarget(entity(EntityTypes.TEXT_DISPLAY) {
                        tag = displayTag
                    })
                    run {
                        data(self())["text"] = components.toNbtTag()
                    }
                }
            }
        }
    }

    context(fn: Function)
    override fun tick() {
        removeInteraction.onInteract {
            add(-step)
        }
        addInteraction.onInteract {
            add(step)
        }
    }
}
