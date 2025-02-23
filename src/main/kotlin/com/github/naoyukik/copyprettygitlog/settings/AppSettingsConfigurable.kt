package com.github.naoyukik.copyprettygitlog.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.COLUMNS_MEDIUM
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.annotations.Nls

/**
 * Provides controller functionality for application settings.
 */
class AppSettingsConfigurable(private val project: Project) : Configurable {
    private val mySettingsState
        get() = AppSettingsState.getInstance(project)

    private val mainPanel: DialogPanel by lazy { createUIComponents() }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Copy Pretty Git Log"
    }

    override fun createComponent(): DialogPanel {
        return mainPanel
    }

    override fun isModified(): Boolean {
        return mainPanel.isModified()
    }

    override fun reset() {
        mainPanel.reset()
    }

    override fun apply() {
        mainPanel.apply()
    }

    private fun createUIComponents(): DialogPanel {
        return panel {
            row("Format:") {
                textField()
                    .bindText(mySettingsState::customPattern1)
                    .columns(COLUMNS_MEDIUM)
                    .comment(
                        "Available placeholders:" +
                            "{AUTHOR_NAME}, {COMMITER_NAME}, {COMMIT_TIME}, {FULL_MESSAGE}, {SUBJECT}"
                    )
            }
            row("Time format:") {
                textField()
                    .bindText(mySettingsState::customTimeFormat)
                    .columns(COLUMNS_MEDIUM)
                    .comment("For time formatting, you can use Java's DateTimeFormatter.ofPattern setting.")
            }
            row {
                checkBox("Reverse order the copied listings")
                    .bindSelected(mySettingsState::reversed)
            }
        }
    }
}
