package hexlet.code.service;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> findAll() {
        return repository.findAll().stream().map(mapper::map).toList();
    }

    public UserDTO findById(Long id) {
        return mapper.map(getUser(id));
    }

    @Transactional
    public UserDTO create(UserCreateDTO data) {
        var user = mapper.map(data);
        user.setPasswordDigest(passwordEncoder.encode(data.getPassword()));
        repository.saveAndFlush(user);
        return mapper.map(user);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO data) {
        var user = getUser(id);
        mapper.update(data, user);
        data.getPassword()
                .ifPresent(password -> user.setPasswordDigest(passwordEncoder.encode(password)));
        repository.saveAndFlush(user);
        return mapper.map(user);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getUser(id));
        repository.flush();
    }

    private User getUser(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with id " + id + " not found"));
    }
}
