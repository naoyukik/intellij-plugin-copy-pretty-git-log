package com.github.naoyukik.intellijplugincopyprettygitlog.domain

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.github.naoyukik.intellijplugincopyprettygitlog.settings.AppSettingsState

class SettingState {
    fun getAppSettingsState(editor: Editor): AppSettingsState? {
        return editor.project?.let { AppSettingsState.getInstance(it) }
    }

    fun getAppSettingsState(e: AnActionEvent): AppSettingsState? {
        return e.project?.getService(AppSettingsState::class.java)
    }
}
