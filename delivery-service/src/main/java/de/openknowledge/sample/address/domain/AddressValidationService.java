/*
 * Copyright 2019 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.sample.address.domain;

import static jakarta.ws.rs.client.Entity.entity;

import java.io.StringReader;
import java.util.Locale;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.johnzon.jaxrs.jsonb.jaxrs.JsonbJaxrsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AddressValidationService {

    private static final Logger LOG = Logger.getLogger(AddressValidationService.class.getSimpleName());
    private static final String ADDRESS_VALIDATION_PATH = "valid-addresses";

    @Inject
    @ConfigProperty(name = "address-validation-service.url")
    String addressValidationServiceUrl;
    @Inject
    Provider<Locale> localeProvider;

    public void validate(Address address) {
        Response validationResult = ClientBuilder
            .newClient()
            .register(JsonbJaxrsProvider.class)
            .target(addressValidationServiceUrl)
            .path(ADDRESS_VALIDATION_PATH)
            .request(MediaType.APPLICATION_JSON)
            .header("Accept-Language", localeProvider.get().getLanguage())
            .post(entity(address, MediaType.APPLICATION_JSON_TYPE));
        if (validationResult.getStatus() != Response.Status.OK.getStatusCode()) {
            LOG.info("validation failed");
            String responseBody = validationResult.readEntity(String.class);
            JsonObject problem = Json.createReader(new StringReader(responseBody)).readObject();

            // Check if "detail" field exists, fallback to full JSON if not present
            String detail = problem.containsKey("detail")
                ? problem.getString("detail")
                : responseBody;
            LOG.info("Error saving address: " + detail);

            throw new ValidationException(detail);
        }
    }
}
