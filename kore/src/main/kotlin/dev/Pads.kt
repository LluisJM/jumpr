package dev

import game.playingTag
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.TranslatedTextComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Axes
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.fill
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.commands.tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snakeCase
import utils.getOrCreateTranslation

class Pad(
    val text: String,
    val block: BlockArgument,
    val size: Int = 2,
    val id: String = text.snakeCase().replace(" ", "_"),
    private val formatting: TranslatedTextComponent.() -> Unit = {}
) {
    fun entityTag() = "pad.${id.replace("_", ".")}"

    context(fn: Function)
    fun set() {
        fn.execute {
            at(self())
            align(Axes(x = true, y = true, z = true))
            run {
                summon(EntityTypes.MARKER, vec3().relative) {
                    this["Tags"] = nbtListOf(entityTag())
                }
                summon(EntityTypes.TEXT_DISPLAY, vec3(0, 2.5, 0).relative) {
                    this["Tags"] = nbtListOf(entityTag())
                    this["text"] = getOrCreateTranslation(entityTag(), text, block = formatting).toNbtTag()
                    this["billboard"] = "vertical"
                }
                fill(vec3(-size / 2, -1, -size / 2).relative, vec3(size - (size / 2) - 1, -1, size - (size / 2) - 1).relative, block)
            }
        }
    }

    context(fn: Function)
    fun tick(block: Function.() -> Unit) {
        fn.execute {
            asTarget(allEntities {
                type = EntityTypes.MARKER
                tag = entityTag()
            })
            at(self())
            run {
                execute {
                    asTarget(allPlayers {
                        distance = range(0..size / 2 + 1)
                    })
                    at(self())
                    ifCondition {
                        block(vec3(0, -1, 0).relative, this@Pad.block)
                    }
                    run(block)
                }
            }
        }
    }
}

fun DataPack.initializePads() {
    val playPad = Pad("Play Game", Blocks.BLUE_CONCRETE, 2, "player.play") {
        color = Color.BLUE
    }
    val spectatePad = Pad("Spectate Game", Blocks.LIGHT_GRAY_CONCRETE, 2, "player.spectate") {
        color = Color.GRAY
    }

    tick {
        playPad.tick {
            tag(self()) {
                add(playingTag)
            }
        }
        spectatePad.tick {
            tag(self()) {
                remove(playingTag)
            }
        }
    }

    listOf(playPad, spectatePad).forEach {
        function("pad/set/${it.id.replace(".", "/")}") {
            it.set()
        }
    }
}