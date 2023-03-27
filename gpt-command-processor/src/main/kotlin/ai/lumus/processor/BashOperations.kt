package ai.lumus.processor

import java.io.BufferedReader
import java.io.InputStreamReader

interface BashOperation : Operation

class CommandOperation(private val bashCommand: String) : BashOperation {

    override fun execute() {
        val commandArray = bashCommand.split(" ").toTypedArray()
        val processBuilder = ProcessBuilder(*commandArray)
        val process = processBuilder.start()

        val stdoutReader = BufferedReader(InputStreamReader(process.inputStream))
        val stderrReader = BufferedReader(InputStreamReader(process.errorStream))

        stdoutReader.lines().forEach { line -> println(line) }
        stderrReader.lines().forEach { line -> System.err.println(line) }

        process.waitFor()
    }
}