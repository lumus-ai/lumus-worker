package ai.lumus.worker.openai

import ai.lumus.worker.command.OperationCommand
import ai.lumus.worker.openai.request.ChatMessage
import ai.lumus.worker.openai.request.ChatModel
import ai.lumus.worker.openai.request.ChatRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import java.io.File

class CommandGptClient(
    openaiApiKey: String,
    model: ChatModel = ChatModel.GPT_3_5_TURBO,
) : AbstractGptClient(openaiApiKey, model) {

    fun generateCommands(prompt: String, filesAware: Set<File>): List<OperationCommand> {
        val requestBody = ChatRequest(
            model = model.id,
            messages = baseInstructions() + ChatMessage(content = userCommandGenerationPrompt(prompt, filesAware)),
        )
        val message = apiService.complete(requestBody).execute().body()!!.choices.first().message
        var content = message.content
        if (message.content.startsWith("{")) { content = "[${message.content.withFixes()}]" }
        val commandsJson = Json.parseToJsonElement(content).jsonArray
        return commandsJson.map { Json.decodeFromJsonElement(it) }
    }

    private fun baseInstructions(): List<ChatMessage> {
        return listOf(
            ChatMessage("system", systemPromptForCommandGeneration()),
            ChatMessage("user", userCommandGenerationPrompt("Create a file with \"Lumus\" one-line content")),
            ChatMessage(
                "assistant",
                """
                    [
                        {"action": "CREATE", "data": {"filename": "file.txt"}},
                        {"action": "WRITE", "data": {"filename": "file.txt", "content": "Lumus"}}
                    ]
                """.trimIndent(),
            ),
        )
    }
}

private fun String.withFixes(): String {
    return this.replace("\$", "\\\$")
}
