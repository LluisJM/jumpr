package utils.item

import game.inGamePlayers
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.helpers.vfx.Shape
import io.github.ayfri.kore.helpers.vfx.drawShape

private const val orbTag = "orb"

class OrbItem(
    name: String,
    description: String,
    val radius: Double,
    dummyItem: ItemArgument = Items.GLASS_BOTTLE,
    defaultCount: Int = 1,
    tags: List<String> = listOf(),
    components: Components.() -> Unit = {},
    val effect: Function.() -> Unit
): BuildPhaseItem(
    name,
    description,
    Type.SPECIAL,
    dummyItem,
    defaultCount,
    Behaviour.KEEP_ON_GROUND,
    tags.plus(orbTag),
    components
) {
    constructor(
        name: String,
        description: String,
        radius: Double,
        effect: MobEffectArgument,
        effectAmplifier: Int,
        dummyItem: ItemArgument = Items.GLASS_BOTTLE,
        defaultCount: Int = 1,
        effectDuration: Int = 1,
        tags: List<String> = listOf(),
        components: Components.() -> Unit = {}
    ) : this(
        name,
        description,
        radius,
        dummyItem,
        defaultCount,
        tags,
        components,
        {
            effect(inGamePlayers {
                distance = range(0.0, radius)
            }) {
                give(effect, effectDuration, effectAmplifier)
            }
        }
    )

    context(fn: Function, dp: DataPack)
    fun showParticles() = asAndAtItem {
        /*
        TODO: add particle settings
        The particles ar at world spawn at the moment due to a Kore limitation.
         */

        val shape = dp.drawShape("${id}_effect") {
            shape = Shape.SPHERE
            particle = Particles.SOUL_FIRE_FLAME
            radius = this@OrbItem.radius
            points = 50
            height = 5.0
            turns = 4
        }

        function(shape)
    }

    context(fn: Function)
    fun applyEffect() = asAndAtItem(effect)
}