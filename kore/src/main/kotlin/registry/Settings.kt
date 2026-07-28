package registry

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.maths.Axes
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.functions.tick
import registry.Settings.Companion.ALL
import utils.setting.AbstractSetting
import utils.setting.BooleanSetting
import utils.setting.IntSetting
import utils.setting.TimeSetting
import java.lang.Math.floorDiv

interface Settings {
    companion object {
        private val _all = mutableListOf<AbstractSetting>()
        val ALL: List<AbstractSetting> get() = _all

        val MAX_ROUNDS = register(IntSetting("Maximum Rounds", 1, 100, 5, "max_rounds"))
        val ROUND_LENGTH = register(TimeSetting("Round Length", 15, 60 * 10, 45))
        val PVP = register(BooleanSetting("PvP", true, "pvp"))

        private fun <T: AbstractSetting> register(setting: T): T {
            _all.add(setting)
            return setting
        }
    }
}

fun DataPack.initializeSettings(): FunctionArgument {
    val settingPerRow = 5
    val verticalSeparation = 0.6
    val horizontalSeparation = 2.1

    tick("settings/handle_interactions") {
        ALL.forEach { setting ->
            setting.tick()
            setting.updateDisplay()
        }
    }

    function("settings/reset") {
        ALL.forEach {
            it.reset()
        }
    }

    return function("settings/setup") {
        kill(allEntities {
            tag = "jumpr.setting"
        })
        val body = generatedFunction("settings/setup_${hashCode()}") {
            ALL.withIndex().forEach { (i, setting) ->
                val z = floorDiv(i, settingPerRow).toDouble() * horizontalSeparation
                val y = (i % settingPerRow).toDouble() * verticalSeparation + 1.0
                setting.summonButton(vec3(0.9, y, z).relative, Axis.Z)
            }
        }

        execute {
            at(self())
            align(Axes(x = true, y = true, z = true))
            run {
                function(body)
            }
        }
    }
}