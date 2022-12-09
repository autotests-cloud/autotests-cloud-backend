package cloud.autotests.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class JiraIssue  implements Serializable {

	@Serial
	private static final long serialVersionUID = -8808249853955081111L;
	private String url;
	private String key;
}
