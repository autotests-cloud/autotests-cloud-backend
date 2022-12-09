package cloud.autotests.backend.models.request;

import cloud.autotests.backend.models.request.enums.*;
import cloud.autotests.backend.validator.UrlConstraint;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Opts implements Serializable {

    @Serial
    private static final long serialVersionUID = 6798547861657425013L;

    @UrlConstraint
    private String url;
    private String title;
    private StackType stack;
    @NotNull
    private Tests tests;
    private List<Visualization> visualization;
    @SerializedName("source_code")
    private SourceCodeType sourceCode;
    private CiType ci;
    @SerializedName("browser_type")
    private BrowserType browse;
    @SerializedName("browsers_hub")
    private BrowserHubType hub;
    private List<NotificationType> notifications;
    @SerializedName("allure_testops")
    private TestOpsType testops;
    @SerializedName("issue_tracker")
    private TrackerType tracker;

    private String price;
    @Email
    @NotNull
    private String email;
}
