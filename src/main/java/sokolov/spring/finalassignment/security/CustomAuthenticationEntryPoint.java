package sokolov.spring.finalassignment.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sokolov.spring.finalassignment.exception.ErrorMessageResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException, ServletException {
        LOGGER.error("Handling authentication exception", authException);
        logIncomingRequest(request, null);
        ErrorMessageResponse messageResponse = new ErrorMessageResponse(
                "Необходима аутентификация",
                authException.getMessage(),
                LocalDateTime.now()
        );

        String stringResponse = objectMapper.writeValueAsString(messageResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(stringResponse);
    }

    private void logIncomingRequest(HttpServletRequest request, Object[] args) {
        try {
            LOGGER.debug("""
                === INCOMING REQUEST ===
                Method: {}
                URL: {}
                Parameters: {}
                Arguments: {}
                Client IP: {}
                """,
                    request.getMethod(),
                    request.getRequestURL(),
                    request.getParameterMap(),
                    objectMapper.writeValueAsString(args),
                    request.getRemoteAddr()
            );
        } catch (Exception e) {
            LOGGER.warn("Failed to log request", e);
        }
    }
}
