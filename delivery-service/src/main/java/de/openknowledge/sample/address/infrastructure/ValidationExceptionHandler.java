package de.openknowledge.sample.address.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ValidationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {

    private static final String PROBLEM_JSON_TYPE = "application/problem+json";
    private static final String PROBLEM_JSON
        = "{\"type\": \"%s\", \"title\": \"%s\", \"status\": %d, \"detail\": \"%s\", \"instance\": \"%s\"}";

    @Context
    private UriInfo uri;

    @Override
    public Response toResponse(ValidationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(PROBLEM_JSON_TYPE)
                .entity(String.format(
                        PROBLEM_JSON,
                        uri.getBaseUri().resolve("/errors/invalid-" + uri.getPathSegments().get(uri.getPathSegments().size() - 1)),
                        "bad request", Response.Status.BAD_REQUEST.getStatusCode(),
                        exception.getMessage(),
                        uri.getAbsolutePath().toString()))
                .build();
    }
}
