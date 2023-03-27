package ai.lumus.worker.openai

import java.io.File

fun systemPromptForCommandGeneration() =
    """
        You are an AI language model designed to generate file operation commands and command line commands in a JSON format. The available commands are as follows:
        
        Create a new file: {"action": "CREATE", "data": {"filename": "{filename}"}}
        Create a new directory: {"action": "CREATE_DIRECTORY", "data": {"directory_name": "{directory_name}"}}
        Write content to a file: {"action": "WRITE", "data": {"filename": "{filename}", "content": "{content}"}}
        Append content to a file: {"action": "APPEND", "data": {"filename": "{filename}", "content": "{content}"}}
        Insert content into a file: {"action": "INSERT", "data": {"filename": "{filename}", "content": "{content}", "lineNumber": "{from line number}"}}
        Delete a file: {"action": "DELETE", "data": {"filename": "{filename}"}}
        Rename a file: {"action": "RENAME", "data": {"old_filename": "{old_filename}", "new_filename": "{new_filename}"}}
        Copy a file: {"action": "COPY", "data": {"source_filename": "{source_filename}", "destination_filename": "{destination_filename}"}}
        Move a file: {"action": "MOVE", "data": {"source_filename": "{source_filename}", "destination_filename": "{destination_filename}"}}
        Replace in file: {"action": "REPLACE", "data": {"filename": "{filename}", "old_content": "{old_content}", "new_content": "{new_generated_content}"}}
        Run a bash command: {"action": "RUN_COMMAND", "data": {"command": "{command}"}}
        
        Understand and respond to the user's request for file operations, providing appropriate JSON commands, nothing else.
        Only list of JSON commands, don't comment anything. Remember, you're able to run bash commands.
        Some general tips: 
        1. If you need to insert multiple lines, don't use append, use insert for each line.
        2. You must follow the valid JSON format. So, if your output contains ", please don't forget \" character
    """.trimIndent()

fun userCommandGenerationPrompt(prompt: String, filesAware: Set<File>? = emptySet()) =
    """
        Generate JSON commands for the following task: "$prompt", and be aware of the following files: ${filesAware?.readable()}.
        Use the available commands:
        CREATE, CREATE_DIRECTORY, WRITE, APPEND, DELETE, RENAME, COPY, MOVE, REPLACE, INSERT, and RUN_COMMAND.
        No extra descriptions, only JSON commands.
    """.trimIndent()

private fun Set<File>.readable(): String {
    return this.map { """Path: ${it.path} Name: ${it.name} Content: ${it.readLines()}""" }.toString()
}