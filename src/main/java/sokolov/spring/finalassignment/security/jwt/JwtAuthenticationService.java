package sokolov.spring.finalassignment.security.jwt;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import sokolov.spring.finalassignment.users.api.UserCredentials;

@Service
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;

    public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtTokenManager jwtTokenManager) {
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
}
