package dev

import game.gameStart
import game.gameStop
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.TranslatedTextComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Axes
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.set
import utils.getOrCreateTranslation
import utils.Interaction

class Button(
    val name: String,
    val formattedName: String,
    val formatting: TranslatedTextComponent.() -> Unit = {},
    val interactionResult: Function.() -> Command
) {
    val interaction = Interaction(name)

    context(fn: Function)
    fun summon(pos: Vec3 = vec3()): Command {
        return fn.execute {
            align(Axes(x = true, y = true, z = true))
            run {
                summon(EntityTypes.TEXT_DISPLAY, vec3(0.0, 0.75, 0.0).relative) {
                    this["text"] = getOrCreateTranslation("button.${this@Button.name}", this@Button.formattedName, block = formatting).toNbtTag()
                    this["billboard"] = "vertical"
                }
                interaction.summon(pos)
            }
        }
    }
}

val buttons = listOf(
    Button("start_game", "Start Game", {
        color = Color.GREEN
    }) {
        function(gameStart)
    },
    Button("stop_game", "Stop Game",{
        color = Color.RED
    }) {
        function(gameStop)
    }
)

fun DataPack.registerInteractions() {
    tick("button/handle") {
        buttons.forEach { button ->
            button.interaction.onInteract(button.interactionResult)

            function("button/place/${button.name}") {
                execute {
                    align(Axes(x = true, y = true, z = true))
                    run {
                        button.summon(vec3(0.5, 1, 0.5))
                    }
                }
            }
        }
    }
}