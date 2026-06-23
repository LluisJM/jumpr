package logic

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.commands.PlaySoundMixer
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.playSound
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.schedules
import io.github.ayfri.kore.commands.stopSound
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.gamestate.GameStateManager
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument

const val startRunPhaseMusic = "music/run_phase/start"
const val startBuildPhaseMusic = "music/build_phase/start"
const val stopBuildPhaseMusic = "music/build_phase/stop"

fun DataPack.generateMusicLogic(states: GameStateManager) {
    val runMusicIntro = SoundEventArgument("music.run_phase.intro", "jumpr") // TODO: Add files
    val runMusicLoop = SoundEventArgument("music.run_phase.loop", "jumpr")
    val runMusicOutro = SoundEventArgument("music.run_phase.outro", "jumpr")
    val buildMusic = SoundEventArgument("music.build_phase", "jumpr")

    function(startBuildPhaseMusic) {
        playMusic(buildMusic)
        schedules.replace(this, 58.5.seconds)
    }

    function(stopBuildPhaseMusic) {
        schedule("${this@generateMusicLogic.name}:$startBuildPhaseMusic").clear()
        stopSound(allPlayers(), PlaySoundMixer.MUSIC, SoundArgument(buildMusic.name, buildMusic.namespace))
    }

    function(startRunPhaseMusic) {
        val playLoop = "music/run_phase/loop"
        val checkForOutro = "music/run_phase/check"

        function(checkForOutro) {
            states.whenState(PRE_BUILD) {
                schedule("${this@generateMusicLogic.name}:$playLoop").clear()
                playMusic(runMusicOutro)
            }
        }

        function(playLoop) {
            playMusic(runMusicLoop)
        }

        playMusic(runMusicIntro)
        for (i in 1..(10 * 60 / 5)) {
            val seconds = i * (16.0 / 3.0)
            schedule(seconds.seconds, "${this@generateMusicLogic.name}:$checkForOutro")
            if (i % 3 == 0) {
                schedule(seconds.seconds, "${this@generateMusicLogic.name}:$playLoop")
            }
        }
    }
}

private fun Function.playMusic(music: SoundEventArgument) {
    execute {
        asTarget(allPlayers())
        at(self())
        run {
            playSound(music, PlaySoundMixer.MUSIC, self(), vec3().relative)
        }
    }
}