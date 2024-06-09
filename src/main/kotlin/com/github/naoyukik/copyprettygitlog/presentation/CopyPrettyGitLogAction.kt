package com.github.naoyukik.copyprettygitlog.presentation

import com.github.naoyukik.copyprettygitlog.domain.SettingState
import com.github.naoyukik.copyprettygitlog.domain.dto.CommitProperty
import com.github.naoyukik.copyprettygitlog.settings.AppSettingsState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.util.indexing.diagnostic.TimeMillis
import com.intellij.vcs.log.VcsCommitMetadata
import com.intellij.vcs.log.VcsLogDataKeys
import java.awt.datatransfer.StringSelection
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CopyPrettyGitLogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val vcsLog = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION) ?: return

        val state = SettingState().getAppSettingsState(e)
        val pattern = state?.myState?.customPattern1?.trim() ?: "- {SUBJECT} {COMMIT_TIME}"

        ApplicationManager.getApplication().executeOnPooledThread {
            val selectedCommits = vcsLog.cachedMetadata
            val isReversed: Boolean = state?.myState?.reversed ?: false
            val updatedCommits = reversedList(selectedCommits, isReversed)
            val formattedLog: String = formatCommits(updatedCommits, pattern, state)
            ApplicationManager.getApplication().invokeLater {
                CopyPasteManager.getInstance().setContents(StringSelection(formattedLog))
            }
        }
    }

    fun formatCommits(commits: List<VcsCommitMetadata>, pattern: String, state: AppSettingsState?): String {
        val timeFormat = state?.myState?.customTimeFormat ?: "yyyy-MM-dd HH:mm:ss"

        return commits.map { commit ->
            var result = pattern
            for (property in CommitProperty.entries) {
                val replacement = when (property) {
                    CommitProperty.AUTHOR_NAME -> commit.author.name
                    CommitProperty.COMMITER_NAME -> commit.committer.name
                    CommitProperty.COMMIT_TIME -> formatedTime(commit.commitTime, timeFormat)
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

    private fun formatedTime(epochMillis: TimeMillis, formatPattern: String): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern(formatPattern)
        return localDateTime.format(formatter)
    }
}
