package sokolov.spring.finalassignment.users.domain;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sokolov.spring.finalassignment.exception.UserAlreadyExistsException;
import sokolov.spring.finalassignment.users.api.UserRegistration;
import sokolov.spring.finalassignment.users.db.UserEntity;
import sokolov.spring.finalassignment.users.db.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(@Valid UserRegistration request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new UserAlreadyExistsException("Пользователь с таким login уже существует");
        }
        String hashPassword = passwordEncoder.encode(request.password());
        UserEntity userToSave = new UserEntity(
                null,
                request.login(),
                hashPassword,
                request.age(),
                UserRole.USER.toString()
        );
        userToSave = userRepository.save(userToSave);
        return getUser(userToSave);
    }


    public User findByLogin(String login) {
        return getUser(
                userRepository.findByLogin(login)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Не найден пользователь с login = %s".formatted(login))
                        )
        );
    }

    private User getUser(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                UserRole.valueOf(userEntity.getRole()),
                userEntity.getAge()
        );
    }

    public User getUserById(@NotNull Long id) {
        return getUser(userRepository.getReferenceById(id));
    }
}
