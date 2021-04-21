package cloud.autotests.backend.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    String login;
    String email;
    public User() {
    }

    public User(String login, String email) {
        this.login = login;
        this.email = email;
    }


}
