package gen

import game.enemyEntities
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
import io.github.ayfri.kore.functions.Function
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
const val finishLineProtectionTag = "level.finish_line"

val levelBottomBorder = InfiniteBorder("level.bottom", Axis.Y, Relation.LESS_THAN)
val levelBottomLimitBorder = InfiniteBorder("level.bottom.limit", Axis.Z, Relation.GREATER_THAN_OR_EQUAL_TO)

fun DataPack.generateLevelLogic() {
    function("level/clear") {
        enemyEntities.forEach {
            kill(allEntities {
                type = it
            })
        }
        clearShulkerBullets()
        execute {
            asTarget(allEntities {
                type = EntityTypes.MARKER
                tag = levelLoadTag
            })
            at(self())
            run {
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
        kill(allEntities {
            type = EntityTypes.INTERACTION
            tag = finishLineProtectionTag
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
                val actualFunction = generatedFunction("${this@function.name}/place_bottom_border") {
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

                function(actualFunction)
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

fun Function.clearShulkerBullets() {
    kill(allEntities {
        type = EntityTypes.SHULKER_BULLET
    })
}