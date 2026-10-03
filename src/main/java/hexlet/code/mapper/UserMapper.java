package hexlet.code.mapper;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.model.User;
import org.springframework.stereotype.Component;

/** Пароль маппер не трогает: его хеширует сервис. */
@Component
public class UserMapper {

    public UserDTO map(User user) {
        var dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public User map(UserCreateDTO data) {
        var user = new User();
        user.setEmail(data.getEmail());
        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        return user;
    }

    public void update(UserUpdateDTO data, User user) {
        data.getEmail().ifPresent(user::setEmail);
        data.getFirstName().ifPresent(user::setFirstName);
        data.getLastName().ifPresent(user::setLastName);
    }
}
