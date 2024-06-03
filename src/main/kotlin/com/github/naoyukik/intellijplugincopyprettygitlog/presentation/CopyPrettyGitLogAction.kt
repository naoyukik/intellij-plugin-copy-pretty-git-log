package com.github.naoyukik.intellijplugincopyprettygitlog.presentation

import com.github.naoyukik.intellijplugincopyprettygitlog.domain.SettingState
import com.github.naoyukik.intellijplugincopyprettygitlog.domain.dto.CommitProperty
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.vcs.log.VcsCommitMetadata
import com.intellij.vcs.log.VcsLogDataKeys
import com.jetbrains.rd.generator.nova.PredefinedType
import java.awt.datatransfer.StringSelection

class CopyPrettyGitLogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val vcsLog = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION) ?: return

        val state = SettingState().getAppSettingsState(e)
        val pattern = state?.myState?.customPattern1?.trim() ?: "- {SUBJECT}"

        ApplicationManager.getApplication().executeOnPooledThread {
            val selectedCommits = vcsLog.cachedMetadata
            val isReversed: Boolean = state?.myState?.reversed ?: false
            val updatedCommits = reversedList(selectedCommits, isReversed)
            val formattedLog: String = formatCommits(updatedCommits, pattern)
            ApplicationManager.getApplication().invokeLater {
                CopyPasteManager.getInstance().setContents(StringSelection(formattedLog))
            }
        }
    }

    fun formatCommits(commits: List<VcsCommitMetadata>, pattern: String): String {
        return commits.map { commit ->
            var result = pattern
            for (property in CommitProperty.entries) {
                val replacement = when (property) {
                    CommitProperty.AUTHOR_NAME -> commit.author.name
                    CommitProperty.COMMITER_NAME -> commit.committer.name
                    CommitProperty.COMMIT_TIME -> commit.commitTime.toString() // Ensure this is in desired format
                    CommitProperty.FULL_MESSAGE -> commit.fullMessage
                    CommitProperty.SUBJECT -> commit.subject
                }
                result = result.replace(property.placeholder, replacement)
            }
            result
        }.joinToString(separator = "\n")
    }

    private fun reversedList(list: List<VcsCommitMetadata>, isReversed: Boolean): List<VcsCommitMetadata> {
        return when (isReversed) {
            true -> list.reversed()
            false -> list
        }
    }
}
