package ai.lumus.processor

import ai.lumus.worker.command.OperationCommand
import ai.lumus.worker.command.OperationCommand.Action.*

interface Operation {

    fun execute()
}

fun createOperation(command: OperationCommand): Operation {
    val data = command.data
    return when (command.action) {
        CREATE -> CreateFileOperation(data["filename"]!!)
        CREATE_DIRECTORY -> CreateDirectoryOperation(data["directoryName"]!!)
        WRITE -> WriteToFileOperation(data["filename"]!!, data["content"]!!)
        APPEND -> AppendToFileOperation(data["filename"]!!, data["content"]!!)
        DELETE -> DeleteFileOperation(data["filename"]!!)
        RENAME -> RenameFileOperation(data["old_filename"]!!, data["new_filename"]!!)
        COPY -> CopyFileOperation(data["source_filename"]!!, data["destination_filename"]!!)
        MOVE -> MoveFileOperation(data["source_filename"]!!, data["destination_filename"]!!)
        REPLACE -> ReplaceInFileOperation(data["filename"]!!, data["old_content"]!!, data["new_content"]!!)
        INSERT -> InsertIntoFileOperation(data["filename"]!!, data["lineNumber"]!!.toInt(), data["content"]!!)
        RUN_COMMAND -> CommandOperation(data["command"]!!)
    }
}
