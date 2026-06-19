package registry

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import registry.Settings.Companion.ALL
import utils.AbstractSetting
import utils.BooleanSetting
import utils.IntSetting
import utils.TimeSetting
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
        for ((i, setting) in ALL.withIndex()) {
            val x = floorDiv(i, settingPerRow).toDouble() * horizontalSeparation
            val y = (i % settingPerRow).toDouble() * verticalSeparation + 0.75
            setting.summonButton(vec3(x, y, 0.0).relative) // TODO: Make this align to XYZ
        }
    }
}