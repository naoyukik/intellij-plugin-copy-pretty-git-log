package com.github.naoyukik.intellijplugincopyprettygitlog.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    companion object {
        private const val TEXT_AREA_ROWS = 1
        private const val TEXT_AREA_COLUMNS = 30
    }

    private var mainPanel: JPanel? = null
    private val customizedPatterns = JBTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS)
    private val customizedTimeFormat = JBTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS)
    private val checkBoxReverse = JCheckBox()

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Format: "), customizedPatterns, 1, false)
            .addComponent(
                JBLabel(
                    "Available placeholders: {AUTHOR_NAME},{COMMITER_NAME},{COMMIT_TIME},{FULL_MESSAGE},{SUBJECT}"
                )
            )
            .addLabeledComponent(JBLabel("Reverse: "), checkBoxReverse)
            .addLabeledComponent(JBLabel("Time format"), customizedTimeFormat)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel? {
        return mainPanel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return customizedPatterns
    }

    fun getCustomizedPatterns(): String {
        return customizedPatterns.text
    }

    fun setCustomizedPatterns(newText: String?) {
        customizedPatterns.text = newText
    }

    fun getCustomizedTimeFormat(): String {
        return customizedTimeFormat.text
    }

    fun setCustomizedTimeFormat(newTimeFormat: String) {
        customizedTimeFormat.text = newTimeFormat
    }

    fun getReversed(): Boolean {
        return checkBoxReverse.isSelected
    }

    fun setReversed(checked: Boolean) {
        checkBoxReverse.isSelected = checked
    }
}
