package utils.item

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.functions.Function

open class GamePhaseItem(
    name: String,
    description: String,
    dummyItem: ItemArgument,
    defaultCount: Int = 1,
    customModel: Boolean = false,
    behaviour: Behaviour = Behaviour.LOCK_IN_INVENTORY,
    tags: List<String> = listOf(),
    components: Components.() -> Unit = {}
): CustomItem(
    name,
    description,
    customModel,
    Color.YELLOW,
    dummyItem,
    defaultCount,
    tags.plus(behaviour.tag),
    components
) {
    context(dp: DataPack, fn: Function)
    override fun initializeTick() {}

    enum class Behaviour(val tag: String) {
        LOCK_IN_INVENTORY("lock_in_inventory"),
        KEEP_ON_GROUND("keep_on_ground"),
        CAN_PICK_UP("can_pick_up")
    }
}
