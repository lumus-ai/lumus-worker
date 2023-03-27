package ai.lumus.worker.command

import ai.lumus.worker.command.OperationCommandFormatter.formatted
import kotlinx.serialization.Serializable

@Serializable
data class OperationCommand(val action: Action, val data: Map<String, String>) {

    enum class Action {
        // File actions
        CREATE, CREATE_DIRECTORY, WRITE, APPEND, DELETE, RENAME, COPY, MOVE, REPLACE, INSERT,

        // Base actions
        RUN_COMMAND,
    }

    override fun toString(): String = this.formatted()
}