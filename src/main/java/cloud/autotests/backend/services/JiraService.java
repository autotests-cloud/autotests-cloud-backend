package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JiraConfig;
import cloud.autotests.backend.exceptions.BadRequestException;
import cloud.autotests.backend.exceptions.ServerException;
import cloud.autotests.backend.models.JiraIssue;
import cloud.autotests.backend.models.request.Test;
import cloud.autotests.backend.models.request.Opts;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cloud.autotests.backend.config.JiraConfig.JIRA_ISSUE_URL_TEMPLATE;
import static java.lang.String.format;

@Service
@AllArgsConstructor
@Slf4j
public class JiraService {

    private static final Long ISSUE_TYPE = 10002L;
    private final JiraRestClient jiraRestClient;
    private final JiraConfig jiraConfig;

    public JiraIssue createTask(String title) {

        JiraIssue jiraIssue = null;

        BasicIssue basicIssue = createIssue(jiraConfig.getProjectKey(), ISSUE_TYPE, title);
        String issueKey = basicIssue.getKey();

        if (issueKey != null) {
            String jiraIssueUrl = format(JIRA_ISSUE_URL_TEMPLATE, jiraConfig.getUrl(), issueKey);
            jiraIssue = new JiraIssue().setKey(issueKey).setUrl(jiraIssueUrl);
        }

        if (jiraIssue == null) {
            log.error("[ERROR] Generate jira issue with title {}", jiraIssue);
            throw new BadRequestException("[ERROR] Generate jira issue with title {}" + jiraIssue);
        }

        return jiraIssue;
    }

    public void updateTask(Opts opts, String issueKey, String githubTestsUrl, Integer telegramChannelPostId) {
        String jenkinsJobUrl = "https://jenkins.autotests.cloud/job/" + issueKey;
        String telegramChannelUrl = "https://t.me/autotests_cloud/" + telegramChannelPostId;
        String content = format(
                "*Price*: %s\n" +
                        "*Email*: %s\n\n" +
                        "*Github code*: %s\n" +
                        "*Jenkins job*: %s\n" +
                        "*Telegram discussion*: %s\n\n" +
                        "*Test steps*: \n" +
                        "{code}%s{code}",
                opts.getPrice(),
                opts.getEmail(),
                githubTestsUrl,
                jenkinsJobUrl,
                telegramChannelUrl,
                opts.getTests().getTest().stream().map(Test::getStep).collect(Collectors.joining("/")));

        updateIssueDescription(issueKey, content);
    }

    private BasicIssue createIssue(String projectKey, Long issueType, String issueSummary) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();

        IssueInput issueInput = new IssueInputBuilder(projectKey, issueType, issueSummary).build();

        return issueClient.createIssue(issueInput).claim();
    }

    private Issue getIssue(String issueKey) {
        return jiraRestClient.getIssueClient().getIssue(issueKey).claim();
    }

    private void assignIssue(String issueKey, String username) {
        IssueInput input = new IssueInputBuilder().setAssigneeName(username).build();
        jiraRestClient.getIssueClient().updateIssue(issueKey, input).claim();
    }

    private void voteForAnIssue(Issue issue) {
        jiraRestClient.getIssueClient().vote(issue.getVotesUri()).claim();
    }

    private int getTotalVotesCount(String issueKey) {
        BasicVotes votes = getIssue(issueKey).getVotes();
        return votes == null ? 0 : votes.getVotes();
    }

    private void addComment(Issue issue, String commentBody) {
        jiraRestClient.getIssueClient().addComment(issue.getCommentsUri(), Comment.valueOf(commentBody));
    }

    private List<Comment> getAllComments(String issueKey) {
        return StreamSupport.stream(getIssue(issueKey).getComments().spliterator(), false)
                .collect(Collectors.toList());
    }

    private void updateIssueDescription(String issueKey, String newDescription) {
        IssueInput input = new IssueInputBuilder().setDescription(newDescription).build();
        try {
            jiraRestClient.getIssueClient().updateIssue(issueKey, input).claim();
        } catch (Exception e) {
            log.error("error update issue " + issueKey, e);
            throw new ServerException("Error update issue " + issueKey);
        }
    }

    private void deleteIssue(String issueKey, boolean deleteSubtasks) {
        jiraRestClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
    }

}
