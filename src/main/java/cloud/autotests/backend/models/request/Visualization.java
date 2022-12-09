package cloud.autotests.backend.models.request;

import cloud.autotests.backend.models.request.enums.AttachmentType;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Visualization implements Serializable {

    @Serial
    private static final long serialVersionUID = 3163261779325546162L;

    @SerializedName("allure_report")
    private boolean allureReport;
    private List<AttachmentType> attachmentTypes;
}
