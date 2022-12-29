package cloud.autotests.backend.models.websocket;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class WebsocketMessage {

	private String prefix;
	private String content;
	private String contentType;
	private String url;
	private String urlText;
}
