package utils.item

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.item.customData
import io.github.ayfri.kore.arguments.components.item.itemName
import io.github.ayfri.kore.arguments.components.item.lore
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.scores.criteriaUsed
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.clear
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import utils.getOrCreateTranslation
import kotlin.Int

const val customItemIdTag = "custom_item_id"

abstract class CustomItem(
    val name: String,
    val description: String,
    val nameColor: Color? = Color.WHITE,
    val dummyItem: ItemArgument,
    val defaultCount: Int = 1,
    val tags: List<String> = listOf(),
    val components: Components.() -> Unit = {}
) {
    val id: String = name.snakeCase().replace(" ", "_")
    fun asItemStack(): ItemStack = itemStack(dummyItem, defaultCount.toShort()) {
        itemName(getName())
        lore(getOrCreateTranslation("item.$id.description", description) {
            color = Color.GRAY
            italic = false
        })
        customData {
            put(customItemIdTag, id.nbt)
            tags.forEach { tag ->
                put(tag, nbt())
            }
        }
        components()
    }
    fun getName(): ChatComponents {
        val components = getOrCreateTranslation("item.$id.name", name) {
            color = nameColor
        }
        return components
    }
    context(fn: Function)
    fun give(target: EntityArgument = self()): Command = fn.give(target, asItemStack().toItemArgument(), defaultCount)

    context(dp: DataPack, fn: Function)
    abstract fun initializeTick()

    context(fn: Function)
    fun asAndAtItem(block: Function.() -> Unit) = fn.execute {
        asTarget(allEntities {
            type = EntityTypes.ITEM
            nbt = nbt {
                this["Item"] = nbt {
                    this["components"] = nbt {
                        this["minecraft:custom_data"] = nbt {
                            this[customItemIdTag] = id
                        }
                    }
                }
            }
        })
        at(self())
        run(block)
    }

    context(dp: DataPack, fn: Function)
    fun onUse(consume: Boolean = false, target: EntityArgument = allPlayers(), block: Function.() -> Unit) = fn.execute {
        val rightClickObjective = scoreboard("right_click_detection")
        dp.load("item/init_right_click_detection") {
            rightClickObjective.create(criteriaUsed(dummyItem))
        }

        asTarget(target)
        ifCondition {
            score(self(), rightClickObjective.name, rangeOrIntStart(1))
        }
        run {
            scoreboard.objective(self(), rightClickObjective.name).reset()
            block()
            if (consume) consumeOne()
        }
    }

    context(fn: Function)
    fun consumeOne() {
        fn.execute {
            unlessCondition {
                isHoldingItem()()
                isHoldingItemInOffhand()()
            }
            run {
                clear(self(), asItemStack().toItemArgument(), 1)
            }
        }
        fn.execute {
            ifCondition(isHoldingItemInOffhand())
            unlessCondition(isHoldingItem())
            run {
                items.replace(self(), WEAPON.OFFHAND, Items.AIR)
            }
        }
        fn.execute {
            ifCondition(isHoldingItem())
            run {
                items.replace(self(), WEAPON.MAINHAND, Items.AIR)
            }
        }
    }

    fun isHoldingItem(): ExecuteCondition.() -> Unit = {
        entity(self {
            nbt = nbt {
                this["SelectedItem"] = nbt {
                    this["components"] = nbt {
                        this["minecraft:custom_data"] = nbt {
                            this[customItemIdTag] = id
                        }
                    }
                }
            }
        })
    }

    fun isHoldingItemInOffhand(): ExecuteCondition.() -> Unit = {
        entity(self {
            nbt = nbt {
                this["equipment"] = nbt {
                    this["offhand"] = nbt {
                        this["components"] = nbt {
                            this["minecraft:custom_data"] = nbt {
                                this[customItemIdTag] = id
                            }
                        }
                    }
                }
            }
        })
    }
}

fun componentWithItemTag(tag: String) = nbt {
    this["minecraft:custom_data"] = nbt {
        this[tag] = nbt()
    }
}
