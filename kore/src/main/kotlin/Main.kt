import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.tags.functionTag
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import logic.generateDebugging
import logic.generateGameLogic
import logic.generateTimer
import logic.registerInteractions
import utils.createLangFile
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

			generateGameLogic()
			registerInteractions()
			generateTimer()

			generateDebugging()

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
		}.generate()
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
