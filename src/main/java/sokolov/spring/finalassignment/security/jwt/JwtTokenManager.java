package sokolov.spring.finalassignment.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import sokolov.spring.finalassignment.users.domain.UserService;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {

    private final long expirationDate;
    private final SecretKey key;

    private final UserService userService;

    public JwtTokenManager(
            @Value("${jwt.lifetime}") long expirationDate,
            @Value("${jwt.secret-key}") String key,
            @Lazy UserService userService) {
        this.expirationDate = expirationDate;
        this.key = Keys.hmacShaKeyFor(key.getBytes());
        this.userService = userService;
    }

    public String generateToken(String login) {

        var user = userService.findByLogin(login);

        return Jwts.builder()
                .subject(login)
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationDate))
                .id(user.id().toString())
                .claim("role", user.role().toString())
                .compact();

    }

    public String getLoginFromToken(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
