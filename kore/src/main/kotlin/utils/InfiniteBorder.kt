package utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.nearestEntity
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.scoreboard.Scoreboard
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set

val xPos = scoreboard("x_pos")
val yPos = scoreboard("y_pos")
val zPos = scoreboard("z_pos")

class InfiniteBorder(
    val name: String,
    val axis: Axis,
    val relation: Relation
) {
    fun markerTag() = "border.$name"

    context(fn: Function)
    fun summonMarker(pos: Vec3) =
        fn.summon(EntityTypes.MARKER, pos) {
            this["Tags"] = nbtListOf(markerTag())
        }

    context(fn: Function)
    fun ifOutside(target: EntityArgument, block: Function.() -> Command) {
        fn.execute {
            asTarget(allEntities {
                type = EntityTypes.MARKER
                tag = markerTag()
            })
            storeResult {
                score(self(), axisObjective().name)
            }
            run {
                data(self())["Pos[${axisIndex()}]"]
            }
        }
        fn.execute {
            asTarget(target)
            at(target)
            ifCondition {
                score(self(),
                    axisObjective().name,
                    nearestEntity {
                        type = EntityTypes.MARKER
                        tag = markerTag() },
                    axisObjective().name,
                    relation)
            }
            run(block)
        }
    }

    fun axisIndex(): Int {
        if (axis == Axis.X) return 0
        else if (axis == Axis.Y) return 1
        return 2
    }

    fun axisObjective(): Scoreboard {
        if (axis == Axis.X) return xPos
        else if (axis == Axis.Y) return yPos
        return zPos
    }
}

fun DataPack.initializeInfiniteBorders() {
    load("border/init") {
        xPos.create()
        yPos.create()
        zPos.create()
    }

    tick {
        fun storeInto(i: Int, objective: Scoreboard) =
            execute {
                asTarget(allPlayers())
                storeResult {
                    score(self(), objective.name)
                }
                run {
                    data(self())["Pos[$i]"]
                }
            }

        storeInto(0, xPos)
        storeInto(1, yPos)
        storeInto(2, zPos)
    }
}