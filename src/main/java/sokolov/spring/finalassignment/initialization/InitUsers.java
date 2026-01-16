package sokolov.spring.finalassignment.initialization;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sokolov.spring.finalassignment.users.db.UserEntity;
import sokolov.spring.finalassignment.users.db.UserRepository;
import sokolov.spring.finalassignment.users.domain.UserRole;

@Component
public class InitUsers {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        initUsers();
    }

    public void initUsers(){

        initUser(UserRole.ADMIN);
        initUser(UserRole.USER);


    }

    private void initUser(UserRole userRole) {
        String login = userRole.toString().toLowerCase();
        if (userRepository.existsByLogin(login)){
            return;
        }
        String hashPassword = passwordEncoder.encode(login);
        UserEntity userToSave = new UserEntity(
                null,
                login,
                hashPassword,
                userRole.toString()
        );
        userRepository.save(userToSave);
    }


}
