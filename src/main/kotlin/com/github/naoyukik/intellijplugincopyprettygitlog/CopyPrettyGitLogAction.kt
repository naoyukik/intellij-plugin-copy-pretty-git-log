import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.vcs.log.VcsCommitMetadata
import com.intellij.vcs.log.VcsLogDataKeys
import java.awt.datatransfer.StringSelection
import java.text.SimpleDateFormat
import java.util.stream.Collectors

class CopyGitLogAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val vcsLog = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION) ?: return

        ApplicationManager.getApplication().executeOnPooledThread {
            val selectedCommits = vcsLog.cachedMetadata
            val formattedLog: String = formatCommits(selectedCommits)
            ApplicationManager.getApplication().invokeLater {
                CopyPasteManager.getInstance().setContents(StringSelection(formattedLog))
            }
        }
    }

    private fun formatCommits(commits: List<VcsCommitMetadata>): String {
        return commits.reversed().stream()
            .map { commit ->
                "- " + commit.subject
            }
            .collect(Collectors.joining("\n"))
    }
}
