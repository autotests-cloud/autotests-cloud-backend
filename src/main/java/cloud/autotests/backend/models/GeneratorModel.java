package cloud.autotests.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class GeneratorModel {

    private String collectionName;
    private Opts opts;

    @Data
    public static class Opts {
        private String url;
        private String stack;
        private Tests tests;
        private Visualization visualization;
        @JsonProperty("source_code")
        private String sourceCode;
        private String ci;
        @JsonProperty("browser_type")
        private String browserType;
        @JsonProperty("browsers_hub")
        private String browsersHub;
        private List<Notification> notification;
        @JsonProperty("test-management")
        private TestManagement testManagement;
        @JsonProperty("issue_tracker")
        private IssueTracker issueTracker;
    }

    @Data
    public static class Tests {
        private List<Automated> automated;
        private List<Manual> manual;
    }

    @Data
    public static class Automated {
        private String name;
        private Boolean generate;
    }

    @Data
    public static class Manual {
        private String title;
        private String steps;
    }

    @Data
    public static class Visualization {
        @JsonProperty("allure-reports")
        private Boolean allureReports;
        private List<Attachments> attachments;
    }

    @Data
    public static class Attachments {
        private String name;
        private Boolean generate;
    }

    @Data
    public static class Notification {
        private String name;
        private Boolean generate;
    }

    @Data
    public static class TestManagement {
        private String name;
        private Boolean generate;
    }

    @Data
    public static class IssueTracker {
        private String name;
        private Boolean generate;
    }

}



