package com.github.naoyukik.copyprettygitlog.domain

import com.github.naoyukik.copyprettygitlog.settings.AppSettingsState
import com.intellij.openapi.actionSystem.AnActionEvent

class SettingState {
    fun getAppSettingsState(e: AnActionEvent): AppSettingsState? {
        return e.project?.getService(AppSettingsState::class.java)
    }
}
