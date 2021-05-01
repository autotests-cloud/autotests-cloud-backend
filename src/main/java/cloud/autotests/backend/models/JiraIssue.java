package cloud.autotests.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class JiraIssue {

	private String url;
	private String key;

}
