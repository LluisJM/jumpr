package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.fill
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.EntityTypes

const val levelStartTag = "level.start"
const val levelBottomTag = "level.bottom"
const val levelLoadTag = "level.load"

fun DataPack.generateLevelLogic() {
    function("level/clear") {
        val actualFunction = generatedFunction("level/clear_${hashCode()}") {
            kill(allEntities {
                tag = levelBottomTag
            })

            for (zMultiplier in 0..5) {
                for (yMultiplier in -1..2) {
                    fill(
                        vec3(-20, 20 * yMultiplier, 10 * zMultiplier).relative,
                        vec3(20, 20 * (yMultiplier + 1), 10 * (zMultiplier + 1)).relative,
                        Blocks.AIR
                    )
                }
            }
        }

        execute {
            asTarget(allEntities {
                type = EntityTypes.MARKER
                tag = levelLoadTag
            })
            at(self())
            run {
                function(actualFunction)
            }
        }
    }
}