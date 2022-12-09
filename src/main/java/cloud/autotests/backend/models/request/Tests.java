package cloud.autotests.backend.models.request;

import cloud.autotests.backend.validator.ListNotEmpty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tests implements Serializable {

    @Serial
    private static final long serialVersionUID = 1340443922667163496L;

    @SerializedName("console_check")
    private boolean consoleCheck;
    @SerializedName("title_check")
    private boolean titleCheck;
    @ListNotEmpty
    private List<Test> test;
}
