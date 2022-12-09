package cloud.autotests.backend.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder(toBuilder = true)
public class TelegramMessage  implements Serializable {

	@Serial
	private static final long serialVersionUID = -7389179831007510900L;
	private Info post;
	private Info chat;

	@Data
	@Builder(toBuilder = true)
	public static class Info {
		private Integer id;
		private String name;
	}
}
