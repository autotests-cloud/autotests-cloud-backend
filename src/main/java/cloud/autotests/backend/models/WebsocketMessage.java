package cloud.autotests.backend.models;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class WebsocketMessage {

	private String content;
	private String contentType;
	private String url;
	private String urlText;

}
