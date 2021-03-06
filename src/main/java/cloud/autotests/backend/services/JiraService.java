package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JiraConfig;
import cloud.autotests.backend.models.JiraIssue;
import cloud.autotests.backend.models.Order;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cloud.autotests.backend.config.JiraConfig.JIRA_ISSUE_URL_TEMPLATE;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class JiraService {
    private static final Logger LOG = LoggerFactory.getLogger(JiraService.class);

    private static final Long ISSUE_TYPE = 10002L;
    private final JiraRestClient jiraRestClient;
    private final JiraConfig jiraConfig;

    public static void main(String[] args) {
//
//        JiraService myJiraClient = new JiraService();
//
//        final String issueKey = myJiraClient.createIssue("TESTS", 10002L, "Your first lesson 1");
//        myJiraClient.updateIssueDescription(issueKey, "This is description from my Jira Client");
//        Issue issue = myJiraClient.getIssue(issueKey);
////        System.out.println(issue.getDescription());
////
////        myJiraClient.voteForAnIssue(issue);
////
////        System.out.println(myJiraClient.getTotalVotesCount(issueKey));
////
//        myJiraClient.addComment(issue, "This is comment from my Jira Client");
//
//        myJiraClient.assignIssue(issueKey, "svasenkov");
////
////        List<Comment> comments = myJiraClient.getAllComments(issueKey);
////        comments.forEach(c -> System.out.println(c.getBody()));
////
////        myJiraClient.deleteIssue(issueKey, true);
//
//        myJiraClient.jiraRestClient.close();
    }

    public JiraIssue createTask(Order order) {
        BasicIssue basicIssue = createIssue(jiraConfig.getProjectKey(), ISSUE_TYPE, order.getTitle());
        String issueKey = basicIssue.getKey();

        if (issueKey != null) {
            String jiraIssueUrl = format(JIRA_ISSUE_URL_TEMPLATE, jiraConfig.getUrl(), issueKey);
            return new JiraIssue().setKey(issueKey).setUrl(jiraIssueUrl);
        }

        return null;
    }

    public Boolean updateTask(Order order, String issueKey, String githubTestsUrl, Integer telegramChannelPostId) {
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
                order.getPrice(), order.getEmail(), githubTestsUrl, jenkinsJobUrl, telegramChannelUrl, order.getSteps());

        return updateIssueDescription(issueKey, content);
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

    private Boolean updateIssueDescription(String issueKey, String newDescription) {
        IssueInput input = new IssueInputBuilder().setDescription(newDescription).build();
        try {
            jiraRestClient.getIssueClient().updateIssue(issueKey, input).claim();
            return true; // todo maybe bad practice
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deleteIssue(String issueKey, boolean deleteSubtasks) {
        jiraRestClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
    }

}
