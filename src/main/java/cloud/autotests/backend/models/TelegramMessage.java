package cloud.autotests.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TelegramMessage {

	private String name;
	private Integer id;

}
