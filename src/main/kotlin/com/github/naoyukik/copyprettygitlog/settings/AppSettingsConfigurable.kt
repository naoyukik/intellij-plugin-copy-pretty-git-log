package com.github.naoyukik.copyprettygitlog.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable(project: Project) : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null
    private var mySettingsState: AppSettingsState? = null

    init {
        mySettingsComponent = AppSettingsComponent()
        mySettingsState = AppSettingsState.getInstance(project)
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Customize Word Separators"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent?.getPanel()
    }

    override fun isModified(): Boolean {
        return mySettingsComponent?.getCustomizedPatterns() != mySettingsState?.myState?.customPattern1 ||
            mySettingsComponent?.getCustomizedTimeFormat() != mySettingsState?.myState?.customTimeFormat ||
            mySettingsComponent?.getReversed() != mySettingsState?.myState?.reversed
    }

    override fun apply() {
        mySettingsState?.myState?.customPattern1 = mySettingsComponent!!.getCustomizedPatterns()
        mySettingsState?.myState?.reversed = mySettingsComponent!!.getReversed()
        mySettingsState?.myState?.customTimeFormat = mySettingsComponent!!.getCustomizedTimeFormat()
    }

    override fun reset() {
        mySettingsComponent?.setCustomizedPatterns(mySettingsState?.myState?.customPattern1)
        mySettingsComponent?.setReversed(mySettingsState?.myState?.reversed ?: false)
        mySettingsComponent?.setCustomizedTimeFormat(mySettingsState?.myState?.customTimeFormat ?: "")
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
