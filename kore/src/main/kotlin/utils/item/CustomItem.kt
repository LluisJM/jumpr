package utils.item

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ItemPredicate
import io.github.ayfri.kore.arguments.components.item.customData
import io.github.ayfri.kore.arguments.components.item.itemModel
import io.github.ayfri.kore.arguments.components.item.itemName
import io.github.ayfri.kore.arguments.components.item.lore
import io.github.ayfri.kore.arguments.components.itemPredicate
import io.github.ayfri.kore.arguments.components.partial
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
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.scoreboard.create
import io.github.ayfri.kore.scoreboard.scoreboard
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import utils.getOrCreateTranslation
import kotlin.Int

const val customItemIdTag = "custom_item_id"
val defaultDummyItem = Items.ECHO_SHARD

abstract class CustomItem(
    val name: String,
    val description: String,
    val customModel: Boolean = false,
    val nameColor: Color? = Color.WHITE,
    val dummyItem: ItemArgument = defaultDummyItem,
    val defaultCount: Int = 1,
    val tags: List<String> = listOf(),
    val components: Components.() -> Unit = {}
) {
    val id: String = name.snakeCase().replace(" ", "_")

    context(dp: DataPack)
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
        if (customModel) itemModel(id, dp.name)
        components()
    }
    fun getName(): ChatComponents {
        val components = getOrCreateTranslation("item.$id.name", name) {
            color = nameColor
        }
        return components
    }
    context(fn: Function, dp: DataPack)
    fun give(target: EntityArgument = self(), count: Int = defaultCount): Command = fn.give(target, asItemStack().toItemArgument(), count)

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

    context(fn: Function, dp: DataPack)
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

    fun asPredicate(block: ItemPredicate.() -> Unit = {}) = itemPredicate {
        block()
        customData {
            this[customItemIdTag] = id
        }
        partial(ItemComponentTypes.CUSTOM_DATA)
    }
}

fun componentWithItemTag(tag: String) = nbt {
    this["minecraft:custom_data"] = nbt {
        this[tag] = nbt()
    }
}

fun itemWithTags(vararg tags: String, block: ItemPredicate.() -> Unit = {}) = itemPredicate {
    block()
    customData {
        tags.forEach { tag ->
            this[tag] = nbt()
        }
    }
    partial(ItemComponentTypes.CUSTOM_DATA)
}
