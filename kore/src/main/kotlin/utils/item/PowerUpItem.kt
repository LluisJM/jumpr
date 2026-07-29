package utils.item

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument

class PowerUpItem(
    name: String,
    description: String,
    val effect: MobEffectArgument,
    val duration: Int? = 5,
    val amplifier: Int? = 2
): GamePhaseItem(
    name,
    description,
    Type.SPECIAL,
    Items.CARROT_ON_A_STICK
) {
    context(dp: DataPack, fn: Function)
    override fun initializeTick() {
        onUse(true) {
            effect(self()) {
                give(effect, duration, amplifier)
            }
        }
    }
}

