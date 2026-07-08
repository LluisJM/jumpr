package gen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.selector.Sort
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.fill
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import utils.InfiniteBorder

const val levelStartTag = "level.start"
const val levelBottomTag = "level.bottom"
const val levelLoadTag = "level.load"

val levelBottomBorder = InfiniteBorder("level.bottom", Axis.Y, Relation.LESS_THAN)
val levelBottomLimitBorder = InfiniteBorder("level.bottom.limit", Axis.Z, Relation.GREATER_THAN_OR_EQUAL_TO)

fun DataPack.generateLevelLogic() {
    function("level/clear") {
        val actualFunction = generatedFunction("level/clear_${hashCode()}") {
            kill(allEntities {
                tag = levelBottomTag
            })

            for (zMultiplier in 0..10) {
                for (yMultiplier in -3..3) {
                    for (xMultiplier in -2..3) {
                        fill(
                            vec3(20 * xMultiplier - 40, 20 * yMultiplier, 10 * zMultiplier).relative,
                            vec3(20 * xMultiplier, 20 * (yMultiplier + 1), 10 * (zMultiplier + 1)).relative,
                            Blocks.AIR
                        )
                    }
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

    function("level/set_borders") {
        levelBottomBorder.killMarkers()
        levelBottomLimitBorder.killMarkers()
        val bottomFinderTag = "bottom_finder"
        kill(allEntities {
            type = EntityTypes.MARKER
            tag = bottomFinderTag
        })

        execute {
            asTarget(allEntities {
                type = EntityTypes.MARKER
                tag = levelStartTag
            })
            at(self())
            run {
                summon(EntityTypes.MARKER, vec3(0, 100, 0).relative) {
                    this["Tags"] = nbtListOf(bottomFinderTag)
                }
            }
        }
        execute {
            asTarget(allEntities {
                type = EntityTypes.MARKER
                tag = bottomFinderTag
            })
            at(self())
            run {
                execute {
                    at(allEntities(true) {
                        type = EntityTypes.MARKER
                        tag = levelBottomTag
                        sort = Sort.FURTHEST
                    })
                    run {
                        levelBottomBorder.summonMarker(vec3(0, -2, 0).relative)
                    }
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
                levelBottomLimitBorder.summonMarker(vec3(0, 0, -1).relative)
            }
        }
    }
}