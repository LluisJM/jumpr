package utils.item

import game.inGamePlayers
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.commands.command
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.helpers.vfx.Shape
import io.github.ayfri.kore.helpers.vfx.VfxShape
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val orbTag = "orb"

class OrbItem(
    name: String,
    description: String,
    val radius: Double,
    defaultCount: Int = 1,
    tags: List<String> = listOf(),
    components: Components.() -> Unit = {},
    val effect: Function.() -> Unit,
    val color: RGB
): GamePhaseItem(
    name,
    description,
    defaultDummyItem,
    defaultCount,
    true,
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
        color: RGB,
        defaultCount: Int = 1,
        effectDuration: Int = 1,
        tags: List<String> = listOf(),
        components: Components.() -> Unit = {}
    ) : this(
        name,
        description,
        radius,
        defaultCount,
        tags,
        components,
        {
            effect(inGamePlayers {
                distance = range(0.0, radius)
            }) {
                give(effect, effectDuration, effectAmplifier)
            }
        },
        color
    )

    context(fn: Function)
    fun showParticles() = asAndAtItem {
        /*
        TODO: add particle settings
        The particles spawn at world spawn at the moment due to a Kore limitation.
         */

        drawSphere(color) {
            shape = Shape.SPHERE
            particle = Particles.DUST
            radius = this@OrbItem.radius
            points = 50
            height = 5.0
            turns = 4
        }
    }

    context(fn: Function)
    fun applyEffect() = asAndAtItem(effect)

    context(dp: DataPack, fn: Function)
    override fun initializeTick() {
        showParticles()
        applyEffect()
    }
}

private fun Function.drawSphere(color: RGB, center: Vec3 = vec3().relative, block: VfxShape.() -> Unit) {
    val cfg = VfxShape().apply(block)
    drawSphere(cfg, color, center)
}

private fun Function.drawSphere(cfg: VfxShape, color: RGB, center: Vec3 = vec3().relative) {
    val goldenAngle = PI * (3.0 - sqrt(5.0))
    for (i in 0 until cfg.points) {
        val y = 1.0 - 2.0 * i / (cfg.points - 1).coerceAtLeast(1)
        val radiusAtY = sqrt(1.0 - y * y)
        val theta = goldenAngle * i
        val x = cos(theta) * radiusAtY * cfg.radius
        val z = sin(theta) * radiusAtY * cfg.radius
        particle(cfg.particle, color, center.plus(vec3(x, y * cfg.radius, z)))
    }
}

private fun Function.particle(particle: ParticleTypeArgument, color: RGB, pos: Vec3? = null) = println(addLine(command("particle",
    literal("${particle.asString()}{scale: 0.8, color:${color.asList()}}"), pos)))

private fun RGB.asList() = listOf(red / 255f, green / 255f, blue / 255f)
