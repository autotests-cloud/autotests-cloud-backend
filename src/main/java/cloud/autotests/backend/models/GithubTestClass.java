package cloud.autotests.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class GithubTestClass  implements Serializable {

	@Serial
	private static final long serialVersionUID = -8106292085436693061L;
	private String url;
	private String urlText;

}
