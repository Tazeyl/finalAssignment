package sokolov.spring.finalassignment.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        // Логирование входящего запроса
        logIncomingRequest(request, joinPoint.getArgs());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        // Логирование исходящего ответа
        logOutgoingResponse(request, result, executionTime);

        return result;
    }

    private void logIncomingRequest(HttpServletRequest request, Object[] args) {
        try {
            LOGGER.info("""
                            === INCOMING REQUEST ===
                            Method: {}
                            URL: {}
                            Parameters: {}
                            Arguments: {}
                            Header: {}
                            Client IP: {}
                            """,
                    request.getMethod(),
                    request.getRequestURL(),
                    request.getParameterMap(),
                    Arrays.stream(args)
                            .map(this::apply)
                            .collect(Collectors.joining(", ")),
                    getHeadersAsString(request),
                    request.getRemoteAddr()
            );
        } catch (Exception e) {
            LOGGER.warn("Failed to log request", e);
        }
    }

    private void logOutgoingResponse(HttpServletRequest request, Object result, long executionTime) {
        try {
            LOGGER.info("""
                            === OUTGOING RESPONSE ===
                            Method: {}
                            URL: {}
                            Response: {}
                            Execution Time: {} ms
                            Status: {}
                            """,
                    request.getMethod(),
                    request.getRequestURL(),
                    objectMapper.writeValueAsString(result),
                    executionTime,
                    "SUCCESS"
            );
        } catch (Exception e) {
            LOGGER.warn("Failed to log response", e);
        }
    }

    private String getHeadersAsString(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers.toString();
    }

    private String apply(Object t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
