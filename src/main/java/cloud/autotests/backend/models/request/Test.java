package cloud.autotests.backend.models.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Test implements Serializable {

    @Serial
    private static final long serialVersionUID = 3163261779325546162L;

    @NotEmpty
    private String title;
    @NotEmpty
    private String step;
}
