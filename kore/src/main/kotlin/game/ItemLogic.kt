package game

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.randomValue
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.commands.tp
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.gamestate.GameStateManager
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.scoreboard.Scoreboard
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import registry.CustomItems
import utils.BuildPhaseItem
import utils.CustomItem
import utils.componentWithItemTag

const val giveItemOptions = "items/give_options"

fun DataPack.generateItemLogic(states: GameStateManager) {
    val itemsAndGiveFunc = mutableMapOf<CustomItem, FunctionArgument>()

    val buildingPoolObjective = scoreboard("pool_1")
    val specialPoolObjective = scoreboard("pool_2")
    val destroyingPoolObjective = scoreboard("pool_3")

    val buildingPool = mutableListOf<CustomItem>()
    val specialPool = mutableListOf<CustomItem>()
    val destroyingPool = mutableListOf<CustomItem>()

    fun CustomItem.giveFunctionName() = "items/give/${id}"

    CustomItems.ALL.forEach { item ->
        val function = function(item.giveFunctionName()) {
            item.give()
            tellraw(allPlayers(), textComponent("Given item ${item.name}"))
        }
        itemsAndGiveFunc[item] = function

        val phaseItem = item as? BuildPhaseItem
        if (phaseItem != null) {
            if (phaseItem.type == BuildPhaseItem.Type.BUILDING) {
                buildingPool += item
            } else if (phaseItem.type == BuildPhaseItem.Type.SPECIAL) {
                specialPool += item
            } else if (phaseItem.type == BuildPhaseItem.Type.DESTROYING) {
                destroyingPool += item
            }
        }
    }

    load("items/setup_objectives") {
        buildingPoolObjective.create()
        specialPoolObjective.create()
        destroyingPoolObjective.create()
    }

    function(giveItemOptions) { // TODO: Give actual options
        fun sendOption(pool: List<CustomItem>, poolObjective: Scoreboard) {
            execute {
                storeResult {
                    score(self(), poolObjective.name)
                }
                run {
                    randomValue(IntRange(0, pool.count() - 1))
                }
            }
            pool.withIndex().forEach { (i, item) ->
                execute {
                    ifCondition {
                        score(self(), poolObjective.name, IntRangeOrInt(null, i))
                    }
                    run {
                        function(item.giveFunctionName())
                    }
                }
            }
        }

        sendOption(buildingPool, buildingPoolObjective)
        sendOption(specialPool, specialPoolObjective)
        sendOption(destroyingPool, destroyingPoolObjective)
    }

    tick("items/handle_items") {
        CustomItems.ORBS.forEach { orb ->
            orb.applyEffect()
            orb.showParticles()
        }

        execute {
            asTarget(allEntities {
                type = EntityTypes.ITEM
                nbt = nbt {
                    this["Item"] = nbt {
                        this["components"] = componentWithItemTag(BuildPhaseItem.Behaviour.LOCK_IN_INVENTORY.tag)
                    }
                }
            })
            run {
                data(self())["PickupDelay"] = 0
                tp(self(), nearestPlayer())
            }
        }
        execute {
            asTarget(allEntities {
                type = EntityTypes.ITEM
                nbt = nbt {
                    this["Item"] = nbt {
                        this["components"] = componentWithItemTag(BuildPhaseItem.Behaviour.KEEP_ON_GROUND.tag)
                    }
                }
            })
            run {
                data(self()).set("PickupDelay", 100)
            }
        }
        states.whenState(BUILD) {
            execute {
                asTarget(allEntities {
                    type = EntityTypes.ITEM
                    nbt = nbt {
                        this["Item"] = nbt {
                            this["components"] = componentWithItemTag(BuildPhaseItem.Behaviour.CAN_PICK_UP.tag)
                        }
                    }
                })
                run {
                    data(self()).set("PickupDelay", buildPhaseDelaySeconds * 20)
                }
            }
        }
        execute {
            asTarget(allEntities {
                type = EntityTypes.ITEM
            })
            run {
                data(self()).set("Age", 0)
            }
        }
    }
}