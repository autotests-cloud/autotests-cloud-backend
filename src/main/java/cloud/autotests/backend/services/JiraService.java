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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraService {

    private final Long ISSUE_TYPE = 10002L;
    private final String PROJECT_KEY = "TESTS";
    @Autowired
    JiraConfig jiraConfig;
    private String username;
    private String password;
    private String jiraUrl;
    private JiraRestClient jiraRestClient;

    private JiraService() {
        this.username = jiraConfig.jiraUsername;
        this.password = jiraConfig.jiraPassword;
        this.jiraUrl = jiraConfig.jiraUrl;
        this.jiraRestClient = getJiraRestClient();
    }

    public static void main(String[] args) throws IOException {

        JiraService myJiraClient = new JiraService();

        final String issueKey = myJiraClient.createIssue("TESTS", 10002L, "Your first lesson 1");
        myJiraClient.updateIssueDescription(issueKey, "This is description from my Jira Client");
        Issue issue = myJiraClient.getIssue(issueKey);
//        System.out.println(issue.getDescription());
//
//        myJiraClient.voteForAnIssue(issue);
//
//        System.out.println(myJiraClient.getTotalVotesCount(issueKey));
//
        myJiraClient.addComment(issue, "This is comment from my Jira Client");


        myJiraClient.assignIssue(issueKey, "svasenkov");
//
//        List<Comment> comments = myJiraClient.getAllComments(issueKey);
//        comments.forEach(c -> System.out.println(c.getBody()));
//
//        myJiraClient.deleteIssue(issueKey, true);

        myJiraClient.jiraRestClient.close();
    }

    public String createTask(Order order) {
        String finalContent = String.format(
                "<u><b>Price</b></u>: %s\n" +
                        "<u><b>Email</b></u>: %s\n\n" +
                        "<u><b>Test steps</b></u>: \n" +
                        "<pre>%s</pre>",
                order.getPrice(), order.getEmail(), order.getTitle());

        String issueKey = createIssue(PROJECT_KEY, ISSUE_TYPE, order.getTitle());
        if (issueKey == null)
            return null;

        updateIssueDescription(issueKey, finalContent);

        return issueKey;
    }


    private String createIssue(String projectKey, Long issueType, String issueSummary) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();

        IssueInput newIssue = new IssueInputBuilder(projectKey, issueType, issueSummary).build();
        if (newIssue == null) // todo maybe not working
            return null;
        return issueClient.createIssue(newIssue).claim().getKey();
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
        jiraRestClient.getIssueClient().updateIssue(issueKey, input).claim();
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
