package hexlet.code.dto.users;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** Представление пользователя наружу: без пароля. */
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate createdAt;
}
