package sokolov.spring.finalassignment.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sokolov.spring.finalassignment.users.domain.User;
import sokolov.spring.finalassignment.users.domain.UserService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);
    private static final int BEARER_LENGTH = 7;

    private final JwtTokenManager jwtTokenManager;

    private final UserService userService;

    public JwtTokenFilter(JwtTokenManager jwtTokenManager, @Lazy UserService userService) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        String jwtToken = authorizationHeader.substring(BEARER_LENGTH);
        String loginFromToken;
        try {
         loginFromToken= jwtTokenManager.getLoginFromToken(jwtToken);
        } catch (Exception e) {
            LOGGER.error("Error while reading jwtToken", e);
            filterChain.doFilter(request,response);
            return;
        }


        User user = userService.findByLogin(loginFromToken);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(user.role().toString()))
        );

        SecurityContextHolder.getContext()
                .setAuthentication(token);

        filterChain.doFilter(request,response);

    }
}
