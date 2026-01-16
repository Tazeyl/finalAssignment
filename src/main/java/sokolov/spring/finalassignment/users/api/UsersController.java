package sokolov.spring.finalassignment.users.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import sokolov.spring.finalassignment.security.jwt.JwtAuthenticationService;
import sokolov.spring.finalassignment.users.domain.User;
import sokolov.spring.finalassignment.users.domain.UserService;

@RestController
@RequestMapping(value = "/users")
public class UsersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersController.class);

    private final UserService userService;

    private final JwtAuthenticationService jwtAuthenticationService;

    public UsersController(UserService userService, JwtAuthenticationService jwtAuthenticationService) {
        this.userService = userService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }


    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid UserRegistration request
    ) {

        LOGGER.info("Get Request signup User login = {}, age = {}", request.login(), request.age());
        User user = userService.registerUser(request);
        return ResponseEntity.ok(to(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> authenticate(
            @RequestBody @Valid UserCredentials userCredentials
    ) {
        LOGGER.info("Get Request sign in User login = {}", userCredentials.login());
        String token = jwtAuthenticationService.authenticateUser(userCredentials);

        return ResponseEntity.ok(new JwtResponse(token));


    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    public ResponseEntity<UserDto> registerUser(
            @PathVariable Long id
    ) {

        LOGGER.info("Get Request  User info id = {}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(to(user));
    }

    private UserDto to(User user){
        return new UserDto(user.id(), user.login(), user.age(), user.role().toString());
    }
}
