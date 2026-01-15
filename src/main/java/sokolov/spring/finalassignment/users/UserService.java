package sokolov.spring.finalassignment.users;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import sokolov.spring.finalassignment.exception.UserAlreadyExistsException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(@Valid SignUpRequest request) {
        if (userRepository.existsByLogin(request.login())){
            throw new UserAlreadyExistsException("Username already taken");
        }
        UserEntity userToSave = new UserEntity(
                null,
                request.login(),
                request.password(),
                UserRole.USER.toString()
        );
        userToSave = userRepository.save(userToSave);
        return new User(userToSave.getId(), userToSave.getLogin(), UserRole.valueOf(userToSave.getRole()));
    }
}
