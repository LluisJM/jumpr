package game

import asFunction
import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.TitleLocation
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.schedules
import io.github.ayfri.kore.commands.stopSound
import io.github.ayfri.kore.commands.title
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.SoundEvents
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import utils.getOrCreateTranslation

const val playRunPhaseMusic = "music/run_phase/play"
const val stopRunPhaseMusic = "music/run_phase/stop"
const val playBuildPhaseMusic = "music/build_phase/play"
const val stopBuildPhaseMusic = "music/build_phase/stop"

const val buildPhaseMusicTitle = "Block-ing Enemies"
const val runPhaseMusicTitle = "Outjumpering Friends"
const val musicAuthor = "AlanDirt"

fun DataPack.generateMusicLogic() {
    val runMusicIntro = SoundEventArgument("music.run_phase.intro", "jumpr")
    val runMusicLoop = SoundEventArgument("music.run_phase.loop", "jumpr")
    val runMusicOutro = SoundEventArgument("music.run_phase.outro", "jumpr")
    val buildMusic = SoundEventArgument("music.build_phase", "jumpr")

    tick {
        SoundEvents.Music.entries.forEach {
            stopSound(allPlayers(), PlaySoundMixer.MUSIC, SoundArgument(it.asId().split(":").last()))
        }
        SoundEvents.Music.Nether.entries.forEach {
            stopSound(allPlayers(), PlaySoundMixer.MUSIC, SoundArgument(it.asId().split(":").last()))
        }
        SoundEvents.Music.Overworld.entries.forEach {
            stopSound(allPlayers(), PlaySoundMixer.MUSIC, SoundArgument(it.asId().split(":").last()))
        }
    }

    function(playBuildPhaseMusic) {
        displayMusicTitle(buildPhaseMusicTitle)
        playMusic(buildMusic)
        schedules.replace(this, 58.5.seconds)
    }

    function(stopBuildPhaseMusic) {
        schedule("${this@generateMusicLogic.name}:$playBuildPhaseMusic").clear()
        stopSound(allPlayers(), PlaySoundMixer.MUSIC, SoundArgument(buildMusic.name, buildMusic.namespace))
    }

    val playLoop = "music/run_phase/loop"

    function(playRunPhaseMusic) {
        function(playLoop) {
            playMusic(runMusicLoop)
            schedules {
                replace(playLoop.asFunction(), 48.seconds)
            }
        }

        playMusic(runMusicIntro)
        schedules {
            replace("${this@generateMusicLogic.name}:$playLoop", 2.seconds)
        }
        displayMusicTitle(runPhaseMusicTitle)
    }
    function(stopRunPhaseMusic) {
        schedules {
            clear("${this@generateMusicLogic.name}:$playLoop")
        }
        stopSound(allPlayers(), PlaySoundMixer.MUSIC, SoundArgument(runMusicLoop.name, runMusicLoop.namespace))
        playMusic(runMusicOutro)
    }
}

private fun Function.playMusic(music: SoundEventArgument) = playForAll(music, PlaySoundMixer.MUSIC)

fun Function.playForAll(sound: SoundEventArgument, source: PlaySoundMixer): Command {
    return execute {
        asTarget(allPlayers())
        at(self())
        run {
            playSound(sound, source, self(), vec3().relative)
        }
    }
}

private fun Function.displayMusicTitle(name: String) {
    title(allPlayers(), TitleLocation.ACTIONBAR, getOrCreateTranslation(
        "music.credit", "Playing %s by %s",
        listOf(textComponent(name), textComponent(musicAuthor))) {
        color = Color.LIGHT_PURPLE
    })
}