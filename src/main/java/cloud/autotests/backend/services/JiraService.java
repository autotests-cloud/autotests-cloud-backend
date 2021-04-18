package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JiraConfig;
import cloud.autotests.backend.models.Order;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraService {

    private final Long ISSUE_TYPE = 10002L;
    private final String PROJECT_KEY = "HOM";

    private final String jiraUrl;
    private final String username;
    private final String password;
    private final JiraRestClient jiraRestClient;

    @Autowired
    public JiraService(JiraConfig jiraConfig) {
        this.jiraUrl = jiraConfig.getJiraUrl();
        this.username = jiraConfig.getJiraUsername();
        this.password = jiraConfig.getJiraPassword();
        this.jiraRestClient = getJiraRestClient();
    }

//    public static void main(String[] args) throws IOException {
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
//    }

    public String createTask(Order order) {
        return createIssue(PROJECT_KEY, ISSUE_TYPE, order.getTitle());
    }

    public Boolean updateTask(Order order, String issueKey, String githubTestsUrl, Integer telegramChannelPostId) {
        String jenkinsJobUrl = "https://jenkins.autotests.cloud/job/" + issueKey;
        String telegramChannelUrl = "https://t.me/autotests_cloud/" + telegramChannelPostId;
        String content = String.format(
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


    private String createIssue(String projectKey, Long issueType, String issueSummary) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();

        IssueInput issueInput = new IssueInputBuilder(projectKey, issueType, issueSummary).build();

        return issueClient.createIssue(issueInput).claim().getKey();
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

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraUri(), this.username, this.password);
    }

    private URI getJiraUri() {
        return URI.create(this.jiraUrl);
    }
}
