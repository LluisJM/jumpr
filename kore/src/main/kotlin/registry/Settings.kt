package registry

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import registry.Settings.Companion.ALL
import utils.AbstractSetting
import utils.BooleanSetting
import utils.IntSetting
import java.lang.Math.floorDiv

interface Settings {
    companion object {
        private val _all = mutableListOf<AbstractSetting>()
        val ALL: List<AbstractSetting> get() = _all

        val MAX_ROUNDS = register(IntSetting("max_rounds", 1, 100, 5))
        val ROUND_TIME = register(IntSetting("round_time", 200, 5000, 1000))
        val PVP = register(BooleanSetting("pvp", true))

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
        for (setting in ALL) {
            execute {
                asTarget(setting.entity(EntityTypes.INTERACTION))
                ifCondition {
                    data(self(), "interaction")
                }
                run {
                    setting.onButtonInteract()
                    data(self()) {
                        remove("interaction")
                    }
                }
            }

            setting.onDisplayUpdate()
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
            val y = (i % settingPerRow).toDouble() * verticalSeparation
            setting.createInteraction(vec3(x, y, 0.0).relative)
        }
    }
}