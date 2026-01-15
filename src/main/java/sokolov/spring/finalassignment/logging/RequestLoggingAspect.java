package sokolov.spring.finalassignment.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingAspect.class);
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
            logger.info("""
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
            logger.warn("Failed to log request", e);
        }
    }

    private void logOutgoingResponse(HttpServletRequest request, Object result, long executionTime) {
        try {
            logger.info("""
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
            logger.warn("Failed to log response", e);
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
}