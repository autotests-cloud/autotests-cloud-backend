package cloud.autotests.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@Jacksonized
public class Order {

    private String price;
    private String email;
    private String steps;
    private String title;
    private String captcha;

}
