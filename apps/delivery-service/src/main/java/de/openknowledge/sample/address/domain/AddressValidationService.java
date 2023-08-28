package de.openknowledge.sample.address.domain;

import static javax.ws.rs.client.Entity.entity;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.ValidationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.johnzon.jaxrs.jsonb.jaxrs.JsonbJaxrsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AddressValidationService {

    private static final Logger LOG = Logger.getLogger(AddressValidationService.class.getSimpleName());
    private static final String ADDRESS_VALIDATION_PATH = "valid-addresses";

    @Inject
    @ConfigProperty(name = "address-validation-service.url")
    String addressValidationServiceUrl;

    public void validate(Address address) {
        Response validationResult = ClientBuilder
                .newClient()
                .register(JsonbJaxrsProvider.class)
                .target(addressValidationServiceUrl)
                .path(ADDRESS_VALIDATION_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(entity(address, MediaType.APPLICATION_JSON_TYPE));
        if (validationResult.getStatus() != Response.Status.OK.getStatusCode()) {
            LOG.info("validation failed");
            JsonObject problem = Json.createReader(new StringReader(validationResult.readEntity(String.class))).readObject();
            throw new ValidationException(problem.getString("detail"));
        }
    }
}
