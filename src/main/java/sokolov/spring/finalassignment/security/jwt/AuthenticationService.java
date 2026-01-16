package sokolov.spring.finalassignment.security.jwt;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sokolov.spring.finalassignment.users.api.UserCredentials;
import sokolov.spring.finalassignment.users.domain.User;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenManager jwtTokenManager) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
    }

    public String authenticateUser(@Valid UserCredentials userCredentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userCredentials.login(),
                        userCredentials.password()
                )
        );

        return jwtTokenManager.generateToken(userCredentials.login());

    }

    public User getCurrentUser(){
        var scHolder = SecurityContextHolder.getContext();
        if (scHolder.getAuthentication() == null){
            throw new IllegalArgumentException("user not authentication");
        }
        return (User) scHolder.getAuthentication().getPrincipal();
    }
}
