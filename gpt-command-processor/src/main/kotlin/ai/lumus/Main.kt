package ai.lumus

import ai.lumus.processor.createOperation
import ai.lumus.settings.Settings
import ai.lumus.settings.SettingsManager
import ai.lumus.worker.command.OperationCommand
import ai.lumus.worker.command.OperationCommandFormatter.AnsiColor.RED
import ai.lumus.worker.openai.CommandGptClient
import ai.lumus.worker.openai.request.ChatModel
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import java.util.Locale
import kotlin.system.exitProcess

class Lumus : CliktCommand(
    help = "Generates and executes custom user commands by prompts, using GPT",
) {

    private val userPrompt by argument(help = "The user prompt to generate commands from", name = "prompt")

    private val changeModel by option("--change-model", help = "Change the GPT model to use").flag(default = false)

    private val forceRun by option(
        "--force-run",
        "-y",
        help = "Run the generated program without asking for confirmation",
    ).flag(default = false)

    private val fileAware by option(
        "--file-aware",
        "-a",
        help = "Provide a file path to make the GPT model aware of its content",
    ).default("")

    private val settingsManager = SettingsManager()

    override fun run() {
        val apiKey = System.getenv("OPENAI_API_KEY")
        if (apiKey == null) { print("${RED}Error: please define OPENAI_API_KEY environment variable"); exitProcess(0) }

        val settings = loadSettings()
        val gptClient = CommandGptClient(apiKey, settings.gptModel)

        val fileAware: File? = if (fileAware.isNotBlank()) File(fileAware) else null
        val generatedCommands = gptClient.generateCommands(userPrompt, setOfNotNull(fileAware))

        if (!forceRun) previewCommands(generatedCommands)

        if (userConfirmsExecution() || forceRun) {
            executeCommands(generatedCommands)
        } else {
            println("Commands execution canceled.")
        }
    }

    private fun loadSettings(): Settings {
        val existingSettings = settingsManager.loadSettings()

        return if (changeModel || existingSettings == null) {
            requestModelSelection()
        } else {
            existingSettings
        }
    }

    private fun requestModelSelection(): Settings {
        println("Please choose the GPT model: ")
        ChatModel.values().forEachIndexed { index, model ->
            println("${index + 1}. $model")
        }
        print("Enter the model number: ")

        val settings = Settings(gptModel = ChatModel.values()[readln().toInt()])
        settingsManager.saveSettings(settings)
        return settings
    }

    private fun previewCommands(commands: List<OperationCommand>) {
        println("Generated commands:")
        commands.forEachIndexed { index, command ->
            println("${index + 1}. $command")
        }
    }

    private fun userConfirmsExecution(): Boolean {
        print("Do you want to execute these commands? (y/n): ")
        val userInput = readlnOrNull()?.lowercase(Locale.getDefault())
        return userInput == "y"
    }

    private fun executeCommands(commands: List<OperationCommand>) {
        commands.forEach { createOperation(it).execute() }
        println("Commands executed successfully.")
    }
}

fun main(args: Array<String>) = Lumus().main(args)
