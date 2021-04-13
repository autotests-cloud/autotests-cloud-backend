package cloud.autotests.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {

    private String price;
    private String email;
    private String content;

}
