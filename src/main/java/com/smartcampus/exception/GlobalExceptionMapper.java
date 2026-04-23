package com.smartcampus.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            WebApplicationException webException = (WebApplicationException) throwable;
            int status = webException.getResponse().getStatus();
            Response.Status statusInfo = Response.Status.fromStatusCode(status);
            String reason = statusInfo != null ? statusInfo.getReasonPhrase() : "Error";
            String message = webException.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = status == 404 ? "Resource not found." : "Request failed.";
            }
            return Response.status(status)
                    .entity(Map.of(
                            "status", status,
                            "error", reason,
                            "message", message
                    ))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", "An unexpected error occurred."
                ))
                .build();
    }
}
