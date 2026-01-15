package sokolov.spring.finalassignment.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {

    private final long expirationDate;
    private final SecretKey key ;

    public JwtTokenManager(
            @Value("${jwt.lifetime}") long expirationDate,
            @Value("${jwt.secret-key}") String key) {
        this.expirationDate = expirationDate;
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String generateToken (String login){

        return Jwts.builder()
                .subject(login)
                .signWith(key)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ expirationDate))
                .compact();

    }

    public String getLoginFromToken(String jwt){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
