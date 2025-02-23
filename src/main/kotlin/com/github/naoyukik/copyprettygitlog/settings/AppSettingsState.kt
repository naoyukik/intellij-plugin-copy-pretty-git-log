package com.github.naoyukik.copyprettygitlog.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "CopyPrettyGitLogState",
    storages = [Storage("CopyPrettyGitLogState.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState.State> {
    val myState = State()

    var customPattern1
        get() = myState.customPattern1
        set(value) {
            myState.customPattern1 = value
        }

    var reversed
        get() = myState.reversed
        set(value) {
            myState.reversed = value
        }

    var customTimeFormat
        get() = myState.customTimeFormat
        set(value) {
            myState.customTimeFormat = value
        }

    class State {
        var customPattern1 = "- {SUBJECT} {COMMIT_TIME}"
        var reversed = false
        var customTimeFormat = "yyyy-MM-dd HH:mm:ss"
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): AppSettingsState {
            return project.getService(AppSettingsState::class.java)
        }
    }

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState.customPattern1 = state.customPattern1.trim()
        myState.reversed = state.reversed
        myState.customTimeFormat = state.customTimeFormat.trim()
    }
}
