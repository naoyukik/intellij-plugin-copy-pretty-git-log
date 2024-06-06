package com.github.naoyukik.copyprettygitlog.domain

import com.github.naoyukik.copyprettygitlog.settings.AppSettingsState
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor

class SettingState {
    fun getAppSettingsState(editor: Editor): AppSettingsState? {
        return editor.project?.let { AppSettingsState.getInstance(it) }
    }

    fun getAppSettingsState(e: AnActionEvent): AppSettingsState? {
        return e.project?.getService(AppSettingsState::class.java)
    }
}
