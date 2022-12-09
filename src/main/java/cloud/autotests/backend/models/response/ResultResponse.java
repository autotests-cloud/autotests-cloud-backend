package cloud.autotests.backend.models.response;

import cloud.autotests.backend.models.JiraIssue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Data
@Builder(toBuilder = true)
public class ResultResponse  implements Serializable {

    @Serial
    private static final long serialVersionUID = -7945192132727621357L;
    private String telegramDiscussionUrl;
    private String jenkinsJobUrl;
    private JiraIssue jiraIssue;
}
