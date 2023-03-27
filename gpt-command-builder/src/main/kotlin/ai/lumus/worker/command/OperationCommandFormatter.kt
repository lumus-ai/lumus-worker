package ai.lumus.worker.command

import ai.lumus.worker.command.OperationCommand.Action.*
import ai.lumus.worker.command.OperationCommandFormatter.AnsiColor.CYAN
import ai.lumus.worker.command.OperationCommandFormatter.AnsiColor.GREEN
import ai.lumus.worker.command.OperationCommandFormatter.AnsiColor.MAGENTA
import ai.lumus.worker.command.OperationCommandFormatter.AnsiColor.RED
import ai.lumus.worker.command.OperationCommandFormatter.AnsiColor.YELLOW

object OperationCommandFormatter {

    fun OperationCommand.formatted(): String {
        return when (action) {
            CREATE -> formattedCreate()
            CREATE_DIRECTORY -> formattedCreateDirectory()
            WRITE -> formattedWrite()
            APPEND -> formattedAppend()
            DELETE -> formattedDelete()
            RENAME -> formattedRename()
            COPY -> formattedCopy()
            INSERT -> formattedInsert()
            MOVE -> formattedMove()
            REPLACE -> formattedReplace()
            RUN_COMMAND -> formattedRunCommand()
        }
    }

    private fun OperationCommand.formattedReplace(): String {
        val filename = data["filename"] ?: ""
        val oldContent = data["old_content"]?.lines() ?: emptyList()
        val newContent = data["new_content"]?.lines() ?: emptyList()
        val maxLines = maxOf(oldContent.size, newContent.size)

        val lines = mutableListOf<String>()
        lines.add("${YELLOW}REPLACE${AnsiColor.RESET} in file `$filename`:")

        for (i in 0 until maxLines) {
            val oldLine = oldContent.getOrNull(i)?.trim() ?: ""
            val newLine = newContent.getOrNull(i)?.trim() ?: ""
            lines.add("\t$RED$oldLine${AnsiColor.RESET}")
            lines.add("\t$GREEN$newLine${AnsiColor.RESET}")
        }

        return lines.joinToString(separator = "\n")
    }

    private fun OperationCommand.formattedCreate(): String {
        val filename = data["filename"] ?: ""
        return "${GREEN}CREATE${AnsiColor.RESET} file: `$filename`"
    }

    private fun OperationCommand.formattedCreateDirectory(): String {
        val dirname = data["dirname"] ?: ""
        return "${GREEN}CREATE_DIRECTORY${AnsiColor.RESET} directory: `$dirname`"
    }

    private fun OperationCommand.formattedWrite(): String {
        val filename = data["filename"] ?: ""
        val content = data["content"]?.toShortenVersion() ?: ""
        return "${CYAN}WRITE${AnsiColor.RESET} to file `$filename`:\n\t$content"
    }

    private fun OperationCommand.formattedAppend(): String {
        val filename = data["filename"] ?: ""
        val content = data["content"]?.toShortenVersion() ?: ""
        return "${CYAN}APPEND${AnsiColor.RESET} to file `$filename`:\n\t$content"
    }

    private fun OperationCommand.formattedInsert(): String {
        val filename = data["filename"] ?: ""
        val lineNumber = data["lineNumber"] ?: ""
        val content = data["content"]?.toShortenVersion() ?: ""
        return "${CYAN}INSERT${AnsiColor.RESET} into file `$filename` from line $lineNumber:\n\t$content"
    }

    private fun OperationCommand.formattedDelete(): String {
        val filename = data["filename"] ?: ""
        return "${RED}DELETE${AnsiColor.RESET} file: `$filename`"
    }

    private fun OperationCommand.formattedRename(): String {
        val filename = data["filename"] ?: ""
        val newFilename = data["new_filename"] ?: ""
        return "${YELLOW}RENAME${AnsiColor.RESET} file `$filename` to `$newFilename`"
    }

    private fun OperationCommand.formattedCopy(): String {
        val filename = data["filename"] ?: ""
        val destination = data["destination"] ?: ""
        return "${YELLOW}COPY${AnsiColor.RESET} file `$filename` to `$destination`"
    }

    private fun OperationCommand.formattedMove(): String {
        val filename = data["filename"] ?: ""
        val destination = data["destination"] ?: ""
        return "${YELLOW}MOVE${AnsiColor.RESET} file `$filename` to `$destination`"
    }

    private fun OperationCommand.formattedRunCommand(): String {
        val command = data["command"] ?: ""
        return "${MAGENTA}COMMAND${AnsiColor.RESET}: `$command`"
    }

    private fun String?.toShortenVersion(): String? {
        if (this == null) return null
        return if (length > 100) { "${substring(0, 97)}..." } else { this }
    }

    object AnsiColor {
        const val RESET = "\u001B[0m"
        const val BLACK = "\u001B[30m"
        const val RED = "\u001B[31m"
        const val GREEN = "\u001B[32m"
        const val YELLOW = "\u001B[33m"
        const val BLUE = "\u001B[34m"
        const val MAGENTA = "\u001B[35m"
        const val CYAN = "\u001B[36m"
        const val WHITE = "\u001B[37m"
    }
}
