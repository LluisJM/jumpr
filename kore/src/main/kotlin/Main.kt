import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.tags.functionTag
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import dev.generateDebugging
import dev.initializePads
import game.generateGameLogic
import game.generateItemLogic
import gen.generateLevelLogic
import game.generateMusicLogic
import game.generatePointLogic
import game.generateTimer
import dev.registerInteractions
import utils.createLangFile
import utils.initializeInfiniteBorders
import java.io.File

const val outputPathFolder = "./out"
const val beetOutputPathFolder = "../beet/kore_out"

fun main() {
	arrayOf(outputPathFolder, beetOutputPathFolder).forEach { out ->
		val dataPack = dataPack("jumpr") {
			val file = File(out)
			deleteDirectory(file)

			path = Path(out)
			SystemFileSystem.createDirectories(path)

			val gameTimer = generateTimer()

			generateLevelLogic()

			generatePointLogic(gameTimer)
			val states = generateGameLogic(gameTimer)
			generateItemLogic(states)

			registerInteractions()
			generateDebugging()

			generateMusicLogic(states)

			initializeInfiniteBorders()
			initializePads()

			arrayOf("tick", "load").forEach { name ->
				functionTag(name, "minecraft") {
					tags.forEach { tag ->
						if (tag.fileName == name) {
							tag.values.forEach { value ->
								add(value.name)
							}
						}
					}
				}
			}
			tags.reverse()

			createLangFile(out)
		}

		dataPack.generate()
	}
}

fun deleteDirectory(directory: File) {
	assert(directory.isDirectory)
	directory.listFiles()?.forEach { file ->
		if (file.isDirectory) {
			deleteDirectory(file)
		} else {
			file.delete()
		}
	}
	directory.delete()
}
