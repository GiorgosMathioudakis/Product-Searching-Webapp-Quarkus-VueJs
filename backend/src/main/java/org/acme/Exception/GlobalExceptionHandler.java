package org.acme.Exception;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    // 1. Catch "User not found" or "Product not found"
    @ServerExceptionMapper
    public Response handleNotFound(EntityNotFoundException e) {
        return buildResponse(404, "Requested resource not found.");
    }

    // 2. Catch "Duplicate SKU" (Database Constraints)
    // Hibernate throws this when a UNIQUE index is violated
    @ServerExceptionMapper
    public Response handleConstraint(ConstraintViolationException e) {
        // We can explicitly check if it's the SKU constraint
        if (e.getConstraintName() != null && e.getConstraintName().contains("sku")) {
            return buildResponse(409, "A product with this SKU already exists.");
        }
        return buildResponse(400, "Database constraint violation.");
    }

    // 3. Catch "Bad Input" (Manual checks like name.length < 3)
    @ServerExceptionMapper
    public Response handleBadInput(IllegalArgumentException e) {
        return buildResponse(400, e.getMessage());
    }

    // 4. Catch "Everything Else" (NullPointer, DB Connection down)
    @ServerExceptionMapper(priority = 1)
    public Response handleSystemError(Throwable t) {
        LOG.error("Critical System Error", t); // Log it for YOU
        return buildResponse(500, "System error. Please contact support."); // Nice message for USER
    }

    // Helper to keep code clean
    private Response buildResponse(int status, String message) {
        return Response.status(status)
                .entity(new ErrorPayload(message, status))
                .build();
    }

    // Simple JSON structure
    public static class ErrorPayload {
        public String error;
        public int status;
        public ErrorPayload(String error, int status) {
            this.error = error;
            this.status = status;
        }
    }
}