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

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

import java.io.StringReader;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.ValidationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import de.openknowledge.sample.customer.domain.CustomerNumber;
import de.openknowledge.sample.jwt.infrastructure.JwtClientFilter;

@ApplicationScoped
public class DeliveryAddressRepository {

    private static final Logger LOG = Logger.getLogger(DeliveryAddressRepository.class.getSimpleName());
    private static final String DELIVERY_ADDRESSES_PATH = "delivery-addresses";

    @Inject
    @ConfigProperty(name = "delivery-service.url")
    String deliveryServiceUrl;

    @Inject
    JsonWebToken token;

    Client client;

    @PostConstruct
    public void newClient() {
        client = ClientBuilder.newClient().register(new JwtClientFilter(token));
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
            .post(entity(deliveryAddress, MediaType.APPLICATION_JSON_TYPE));
        handleValidationError(response);
    }

    private void handleValidationError(Response response) {
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return;
        }
        if (!response.hasEntity()) {
            throw new ValidationException("invalid address");
        }
        JsonObject problem = Json.createReader(new StringReader(response.readEntity(String.class))).readObject();
        throw new ValidationException(problem.getString("detail"));
    }
}
