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

import java.util.Optional;
import java.util.logging.Logger;

import org.apache.johnzon.jaxrs.jsonb.jaxrs.JsonbJaxrsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.customer.domain.CustomerNumber;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class BillingAddressRepository {

    private static final Logger LOG = Logger.getLogger(BillingAddressRepository.class.getSimpleName());
    private static final String BILLING_ADDRESSES_PATH = "billing-addresses";

    @Inject
    @ConfigProperty(name = "billing-service.url")
    String billingServiceUrl;

    Client client;

    @PostConstruct
    public void newClient() {
        client = ClientBuilder.newClient();
    }

    public Optional<Address> find(CustomerNumber customerNumber) {
        LOG.info("load billing address from " + billingServiceUrl);
        return Optional.of(client
            .register(JsonbJaxrsProvider.class)
            .target(billingServiceUrl)
            .path(BILLING_ADDRESSES_PATH)
            .path(customerNumber.toString())
            .request(MediaType.APPLICATION_JSON)
            .get())
            .filter(r -> r.getStatusInfo().getFamily() == SUCCESSFUL)
            .filter(Response::hasEntity)
            .map(r -> r.readEntity(Address.class));
    }

    public void update(CustomerNumber customerNumber, Address billingAddress) {
        LOG.info("update billing address at " + billingServiceUrl);
        client.target(billingServiceUrl)
            .path(BILLING_ADDRESSES_PATH)
            .path(customerNumber.toString())
            .request(MediaType.APPLICATION_JSON)
            .post(entity(billingAddress, MediaType.APPLICATION_JSON_TYPE));
    }
}
