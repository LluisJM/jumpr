package game

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.nearestPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.particle.ParticleMode
import io.github.ayfri.kore.commands.particle.particle
import io.github.ayfri.kore.commands.randomValue
import io.github.ayfri.kore.commands.tp
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.gamestate.GameStateManager
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.scoreboard.Scoreboard
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import registry.CustomItems
import utils.item.GamePhaseItem
import utils.item.CustomItem
import utils.item.componentWithItemTag

const val giveBuildPhaseItems = "items/give_build_phase"

fun DataPack.generateItemLogic(states: GameStateManager) {
    val buildingPoolObjective = scoreboard("pool_1")
    val specialPoolObjective = scoreboard("pool_2")
    val destroyingPoolObjective = scoreboard("pool_3")

    CustomItems.ALL.forEach { item ->
        function(item.giveFunctionName()) {
            item.give()
        }
    }

    load("items/setup_objectives") {
        buildingPoolObjective.create()
        specialPoolObjective.create()
        destroyingPoolObjective.create()
    }

    function(giveBuildPhaseItems) {
        giveFromPool(CustomItems.BUILDING_POOL, buildingPoolObjective)
        giveFromPool(CustomItems.SPECIAL_POOL, specialPoolObjective)
        giveFromPool(CustomItems.DESTROYING_POOL, destroyingPoolObjective)
    }

    tick("items/handle_behaviour") {
        CustomItems.ALL.forEach { item ->
            item.initializeTick()
        }

        CustomItems.COIN.asAndAtItem {
            val dispersion = 0.5
            particle(Particles.ELECTRIC_SPARK, vec3(0, 0.5, 0).relative, vec3(dispersion, dispersion, dispersion), 0.1, 1, ParticleMode.NORMAL, allPlayers())
        }
    }

    tick("items/handle_entities") {
        execute {
            asTarget(allEntities {
                type = EntityTypes.ITEM
                nbt = nbt {
                    this["Item"] = nbt {
                        this["components"] = componentWithItemTag(GamePhaseItem.Behaviour.LOCK_IN_INVENTORY.tag)
                    }
                }
            })
            at(self())
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
                        this["components"] = componentWithItemTag(GamePhaseItem.Behaviour.KEEP_ON_GROUND.tag)
                    }
                }
            })
            run {
                data(self())["PickupDelay"] = 100
            }
        }
        states.whenState(BUILD) {
            execute {
                asTarget(allEntities {
                    type = EntityTypes.ITEM
                    nbt = nbt {
                        this["Item"] = nbt {
                            this["components"] = componentWithItemTag(GamePhaseItem.Behaviour.CAN_PICK_UP.tag)
                        }
                    }
                })
                run {
                    data(self())["PickupDelay"] = buildPhaseDelaySeconds * 20
                }
            }
        }
        execute {
            asTarget(allEntities {
                type = EntityTypes.ITEM
            })
            run {
                data(self())["Age"] = 0
            }
        }
    }
}

private fun CustomItem.giveFunctionName() = "items/give/${id}"

private fun Function.giveFromPool(pool: List<CustomItem>, poolObjective: Scoreboard) {
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