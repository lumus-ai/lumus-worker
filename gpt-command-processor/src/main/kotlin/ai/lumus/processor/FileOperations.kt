package ai.lumus.processor

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

interface FileOperation : Operation

class CreateFileOperation(private val filename: String) : FileOperation {

    override fun execute() {
        File(filename).createNewFile()
    }
}

class CreateDirectoryOperation(private val directoryName: String) : FileOperation {

    override fun execute() {
        File(directoryName).mkdir()
    }
}

class WriteToFileOperation(private val filename: String, private val content: String) : FileOperation {

    override fun execute() {
        File(filename).writeText(content.replace("\\n", "\n"))
    }
}

class ReplaceInFileOperation(
    private val filename: String,
    private val oldContent: String,
    private val newContent: String,
) : FileOperation {

    override fun execute() {
        val file = File(filename)
        val fileLines = file.readLines()
        val oldLines = oldContent.lines()
        val newLines = newContent.lines()

        val updatedLines = mutableListOf<String>()
        var i = 0
        while (i < fileLines.size) {
            if (fileLines.subList(i, i + oldLines.size) == oldLines) {
                updatedLines.addAll(newLines)
                i += oldLines.size
            } else {
                updatedLines.add(fileLines[i])
                i++
            }
        }

        Files.write(Paths.get(filename), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}

class InsertIntoFileOperation(
    private val filename: String,
    private val lineNumber: Int,
    private val content: String,
) : FileOperation {

    override fun execute() {
        val file = File(filename)
        val fileLines = file.readLines()

        if (lineNumber > fileLines.size || lineNumber < 0) {
            throw IllegalArgumentException("Invalid line number: $lineNumber")
        }

        val updatedLines = fileLines.toMutableList()
        updatedLines.add(lineNumber - 1, content)

        file.writeText(updatedLines.joinToString(separator = "\n"))
    }
}

class AppendToFileOperation(private val filename: String, private val content: String) : FileOperation {
    override fun execute() {
        File(filename).appendText("\n" + content.replace("\\n", "\n"))
    }
}

class ReadFile(private val filename: String) : FileOperation {
    override fun execute() {
        println(File(filename).readText())
    }
}

class DeleteFileOperation(private val filename: String) : FileOperation {
    override fun execute() {
        File(filename).delete()
    }
}

class RenameFileOperation(private val oldFilename: String, private val newFilename: String) : FileOperation {
    override fun execute() {
        Files.move(Paths.get(oldFilename), Paths.get(newFilename))
    }
}

class CopyFileOperation(private val sourceFilename: String, private val destinationFilename: String) : FileOperation {
    override fun execute() {
        Files.copy(Paths.get(sourceFilename), Paths.get(destinationFilename))
    }
}

class MoveFileOperation(private val sourceFilename: String, private val destinationFilename: String) : FileOperation {
    override fun execute() {
        Files.move(Paths.get(sourceFilename), Paths.get(destinationFilename))
    }
}