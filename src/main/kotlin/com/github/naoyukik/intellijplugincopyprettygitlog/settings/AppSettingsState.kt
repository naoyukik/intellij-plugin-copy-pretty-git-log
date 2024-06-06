package com.github.naoyukik.intellijplugincopyprettygitlog.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(
    name = "CopyPrettyGitLogState",
    storages = [Storage("CopyPrettyGitLogState.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState.State> {
    val myState = State()

    class State {
        var customPattern1 = "- {SUBJECT} {COMMIT_TIME}"
        var reversed = false
        var customTimeFormat = "yyyy-MM-dd HH:mm:ss"
    }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): AppSettingsState? {
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
