package utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.TranslatedTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.translatedTextComponent
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.json.Json
import java.io.File

val translations = HashMap<String, String>()

fun getOrCreateTranslation(
    key: String,
    value: String? = null,
    with: List<ChatComponents>? = null,
    block: TranslatedTextComponent.() -> Unit = {}
): ChatComponents {
    val key = "jumpr.$key"

    if (translations.contains(key)) {
        assert(value == null) { "Translation key '$key' already exists" }
    } else {
        assert(value != null) { "Translation key '$key' doesn't exist and value is null" }
        translations[key] = value?: ""
    }

    return translatedTextComponent(key, with, translations[key], block)
}

context(dp: DataPack)
fun createLangFile(out: String) {
    val actualOut = "$out/${dp.name}/assets/${dp.name}/lang"

    val path = Path(actualOut)
    SystemFileSystem.createDirectories(path)

    val file = File("$actualOut/en_us.json")

    file.createNewFile()
    file.writeText(Json.encodeToString(translations))
}