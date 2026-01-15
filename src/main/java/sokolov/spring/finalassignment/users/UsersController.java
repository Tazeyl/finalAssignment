package sokolov.spring.finalassignment.users;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UsersController {

    private final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest request
    ) {

        logger.info("Get Request sign in User login = {}", request.login());
        User user = userService.registerUser(request);
        return ResponseEntity.ok(to(user));
    }

    private UserDto to(User user){
        return new UserDto(user.login());
    }
}
