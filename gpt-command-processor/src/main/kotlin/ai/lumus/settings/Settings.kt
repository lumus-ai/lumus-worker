package ai.lumus.settings

import ai.lumus.worker.openai.request.ChatModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths

class SettingsManager {

    private val json = Json { encodeDefaults = false }
    private val settingsFile = getDefaultSettingsFile()

    fun loadSettings(): Settings? {
        return if (settingsFile.exists()) {
            FileReader(settingsFile).use { reader ->
                json.decodeFromString(reader.readText())
            }
        } else { null }
    }

    fun saveSettings(settings: Settings) {
        FileWriter(settingsFile).use { writer ->
            writer.write(json.encodeToString(settings))
        }
    }

    private fun getDefaultSettingsFile(): File {
        val homeDir = System.getProperty("user.home")
        val configDir = Paths.get(homeDir, ".lumus").toFile()

        if (!configDir.exists()) {
            configDir.mkdir()
        }

        return File(configDir, "settings.json")
    }
}

@Serializable
data class Settings(val gptModel: ChatModel)
