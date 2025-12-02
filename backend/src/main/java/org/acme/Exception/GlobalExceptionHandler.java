package org.acme.Exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable>{

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {

        // 1. Handle JAX-RS specific errors (404 Not Found, 403 Forbidden, etc.)
        // This allows standard framework errors to pass through with their original codes
        if (exception instanceof WebApplicationException webEx) {
            Response original = webEx.getResponse();
            // You can decide to wrap this in your JSON format or return as is
            return buildResponse(original.getStatus(), exception.getMessage());
        }

        // 2. Handle Validation Errors (e.g. @NotNull, @Size, Duplicate SKU)
        if (exception instanceof ConstraintViolationException cve) {
            return handleValidationException(cve);
        }

        // 3. Handle Manual Bad Requests (e.g. "Name too short")
        if (exception instanceof IllegalArgumentException) {
            return buildResponse(400, exception.getMessage());
        }

        // 4. Handle Everything Else (500)
        logger.error("Unknown error occurred", exception); // Log full stack trace for you
        return buildResponse(500, "Internal Server Error. Please contact support.");
    }

    private Response handleValidationException(ConstraintViolationException cve) {
        // If it's a Duplicate SKU error (from Database), the message usually contains the constraint name
        // However, standard Bean Validation (@NotNull) comes here too.

        // Collect all validation messages into one string
        String violations = cve.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        // Check for specific keywords if you want to be smart
        if (violations.isEmpty()) {
            // Fallback for Database constraints that get wrapped here
            return buildResponse(409, "Database constraint violation (e.g. Duplicate SKU).");
        }

        return buildResponse(400, violations);
    }

    // Helper to standardise the JSON format
    private Response buildResponse(int status, String message) {
        return Response.status(status)
                .entity(new ErrorPayload(message, status))
                .build();
    }

    // This ensures your Frontend receives the exact JSON structure it expects
    public static class ErrorPayload {
        public String error; // Matches error.response.data.error
        public int status;

        public ErrorPayload(String error, int status) {
            this.error = error;
            this.status = status;
        }
    }

}
