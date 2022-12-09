package cloud.autotests.backend.models.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4010940985272445576L;

    @NotEmpty
    @Size(min = 3, max = 50)
    private String collectionName;
    @NotEmpty
    private String captcha;
    @NotNull
    private @Valid Opts opts;
}
