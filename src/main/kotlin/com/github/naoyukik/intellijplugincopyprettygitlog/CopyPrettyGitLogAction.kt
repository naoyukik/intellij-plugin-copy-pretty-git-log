import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.vcs.log.VcsCommitMetadata
import git4idea.GitUtil
import git4idea.history.GitHistoryUtils
import java.awt.datatransfer.StringSelection
import java.text.SimpleDateFormat
import java.util.stream.Collectors

class CopyGitLogAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // Get the current project
        val project = e.project

        // Get the Git repository for the current project
        val repositoryManager = GitUtil.getRepositoryManager(project!!)
        val repository = repositoryManager.getRepositoryForFileQuick(e.getData(CommonDataKeys.VIRTUAL_FILE))

        ApplicationManager.getApplication().executeOnPooledThread {
            // Get the Git log
            val commits = GitHistoryUtils.history(project, repository!!.root)

            // Format the Git log
            val formattedLog = formatCommits(commits)

            // Copy the formatted log to the clipboard
            ApplicationManager.getApplication().invokeLater {
                CopyPasteManager.getInstance().setContents(StringSelection(formattedLog))
            }
        }
    }

    private fun formatCommits(commits: List<VcsCommitMetadata>): String {
        // Implement your custom formatting here
        return commits.stream()
            .map { commit ->
                "- " + commit.fullMessage + " by " + commit.author + " on " + SimpleDateFormat("yyyy/MM/dd HH:mm").format(
                    commit.authorTime
                ) + " (Commit ID: " + commit.id.asString() + ")"
            }
            .collect(Collectors.joining("\n"))
    }
}
