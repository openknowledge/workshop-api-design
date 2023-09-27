/*
 * Copyright 2019 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.apache.johnzon.jaxrs.jsonb.jaxrs.JsonbJaxrsProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.openknowledge.sample.customer.domain.Customer;
import de.openknowledge.sample.customer.domain.CustomerNumber;

@ApplicationScoped
public class BillingAddressRepository {

    private static final Logger LOG = Logger.getLogger(BillingAddressRepository.class.getSimpleName());
    private static final String BILLING_ADDRESSES_PATH = "billing-addresses";

    private Map<CustomerNumber, Address> billingAddresses;

    @Inject
    @ConfigProperty(name = "billing-service.url")
    String billingServiceUrl;

    Client client;

    @PostConstruct
    public void initialize() {
        initializeCache();
        initializeClient();
    }

    void initializeCache() {
        billingAddresses = new ConcurrentHashMap<>();
    }
    
    void initializeClient() {
        client = ClientBuilder.newClient();
        WebTarget sseUrl = client.target(billingServiceUrl).path(BILLING_ADDRESSES_PATH);
        try (SseEventSource sseStream = SseEventSource.target(sseUrl).build()) {
            sseStream.register(this::update);
            sseStream.open();
        }
    }

    public Optional<Address> find(CustomerNumber customerNumber) {
        LOG.info("load billing address from " + billingServiceUrl);
        if (billingAddresses.containsKey(customerNumber)) {
            return Optional.of(billingAddresses.get(customerNumber));
        }
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
        store(customerNumber, billingAddress);
    }

    public void update(InboundSseEvent event) {
        Customer customer = event.readData(Customer.class);
        store(customer.getNumber(), customer.getBillingAddress());
    }

    private void store(CustomerNumber customer, Address billingAddress) {
        billingAddresses.put(customer, billingAddress);
    }
}
