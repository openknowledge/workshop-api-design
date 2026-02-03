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
import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

import java.io.StringReader;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.customer.domain.CustomerNumber;

@ApplicationScoped
public class DeliveryAddressRepository {

    private static final Logger LOG = Logger.getLogger(DeliveryAddressRepository.class.getSimpleName());
    private static final String DELIVERY_ADDRESSES_PATH = "delivery-addresses";

    @Inject
    @ConfigProperty(name = "delivery-service.url")
    String deliveryServiceUrl;
    @Inject
    Provider<Locale> localeProvider;

    Client client;

    @PostConstruct
    public void newClient() {
        client = ClientBuilder.newClient();
    }

    public Optional<Address> find(CustomerNumber customerNumber) {
        LOG.info("load delivery address from " + deliveryServiceUrl);
        return Optional.of(client
            .target(deliveryServiceUrl)
            .path(DELIVERY_ADDRESSES_PATH)
            .path(customerNumber.toString())
            .request(MediaType.APPLICATION_JSON)
            .get())
            .filter(r -> r.getStatusInfo().getFamily() == SUCCESSFUL)
            .filter(Response::hasEntity)
            .map(r -> r.readEntity(Address.class));
    }

    public void update(CustomerNumber customerNumber, Address deliveryAddress) {
        LOG.info("update delivery address at " + deliveryServiceUrl);
        Response response = client
            .target(deliveryServiceUrl)
            .path(DELIVERY_ADDRESSES_PATH)
            .path(customerNumber.toString())
            .request(MediaType.APPLICATION_JSON)
            .header("Accept-Language", localeProvider.get().getLanguage())
            .post(entity(deliveryAddress, MediaType.APPLICATION_JSON_TYPE));
        handleValidationError(response);
    }

    private void handleValidationError(Response response) {
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return;
        }
        if (!response.hasEntity()) {
            throw new ValidationException("invalid address: empty");
        }

        try {
            String responseBody = response.readEntity(String.class);
            JsonObject problem = Json.createReader(new StringReader(responseBody)).readObject();

            // Check if "detail" field exists, fallback to full JSON if not present
            String detail = problem.containsKey("detail")
                ? problem.getString("detail")
                : responseBody;
            LOG.info("Error saving address: " + detail);

            throw new ValidationException(detail);
        } catch (ValidationException e) {
            throw e; // Re-throw ValidationException
        } catch (Exception e) {
            // If JSON parsing fails, throw a generic validation exception
            LOG.warning("Failed to parse problem JSON from delivery service: " + e.getMessage());
            throw new ValidationException("invalid address - delivery service validation failed");
        }
    }
}
