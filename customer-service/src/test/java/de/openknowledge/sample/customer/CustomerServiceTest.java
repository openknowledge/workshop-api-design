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
package de.openknowledge.sample.customer;

import static de.openknowledge.sample.customer.JsonObjectComparision.sameAs;
import static de.openknowledge.sample.customer.JsonObjectComparision.thatIsSameAs;
import static javax.ws.rs.client.Entity.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.BillingAddressRepository;
import de.openknowledge.sample.address.domain.DeliveryAddressRepository;
import de.openknowledge.sample.customer.domain.CustomerNumber;
import rocks.limburg.cdimock.MockitoBeans;

@MockitoBeans(types = {BillingAddressRepository.class, DeliveryAddressRepository.class})
@MeecrowaveConfig
public class CustomerServiceTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @Inject
    private BillingAddressRepository billingAddressRepository;

    @Inject
    private DeliveryAddressRepository deliveryAddressRepository;

    private URI uri;

    @BeforeEach
    public void setUp() {
        when(deliveryAddressRepository.find(new CustomerNumber("0815")))
            .thenReturn(Optional.of(Address.of("Max Mustermann").atStreet("Poststrasse 1").inCity("26122 Oldenburg").build()));
        when(deliveryAddressRepository.find(new CustomerNumber("0816")))
            .thenReturn(Optional.of(Address.of("Erika Mustermann").atStreet("II. Hagen 7").inCity("45127 Essen").build()));

        when(billingAddressRepository.find(new CustomerNumber("0815")))
            .thenReturn(Optional.of(Address.of("Max Mustermann").atStreet("Poststrasse 1").inCity("26122 Oldenburg").build()));
        when(billingAddressRepository.find(new CustomerNumber("007")))
            .thenReturn(Optional.of(Address.of("Sherlock Holmes").atStreet("221B Baker Street").inCity("London NW1 6XE").build()));

        uri = URI.create("http://localhost:" + config.getHttpPort());
    }

    @Test
    public void getCustomers() {
        JsonArray result = Json.createReader(new StringReader(ClientBuilder
            .newClient()
            .target(uri)
            .path("customers")
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(String.class)))
            .readArray();
        assertThat(result).haveAtLeastOne(thatIsSameAs(getClass().getResourceAsStream("max.json")));
        assertThat(result).haveAtLeastOne(thatIsSameAs(getClass().getResourceAsStream("erika.json")));
        assertThat(result).haveAtLeastOne(thatIsSameAs(getClass().getResourceAsStream("james.json")));
    }

    @Test
    public void getCustomerWithAddresses() {
        JsonObject result = Json.createReader(new StringReader(ClientBuilder
            .newClient()
            .target(uri)
            .path("customers")
            .path("0815")
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(String.class)))
            .readObject();
        assertThat(result).is(sameAs(getClass().getResourceAsStream("max-with-addresses.json")));
    }

    @Test
    public void createCustomer() {
        Response response = ClientBuilder
            .newClient()
            .target(uri)
            .path("customers")
            .request(MediaType.APPLICATION_JSON)
            .post(entity(getClass().getResourceAsStream("sherlock.json"), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

        JsonObject result = Json.createReader(new StringReader(ClientBuilder
            .newClient()
            .target(response.getLocation())
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(String.class)))
            .readObject();
        assertThat(result).is(sameAs(getClass().getResourceAsStream("sherlock.json")));

        JsonArray customers = Json.createReader(new StringReader(ClientBuilder
            .newClient()
            .target(uri)
            .path("customers")
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(String.class)))
            .readArray();
        assertThat(customers).haveAtLeastOne(thatIsSameAs(getClass().getResourceAsStream("sherlock.json")));
    }

    @Test
    public void getCustomerWithBillingAddress() {
        JsonObject result = Json.createReader(new StringReader(ClientBuilder
            .newClient()
            .target(uri)
            .path("customers")
            .path("007")
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(String.class)))
            .readObject();
        assertThat(result).is(sameAs(getClass().getResourceAsStream("james-with-addresses.json")));
    }

    @Test
    public void getCustomerWithDeliveryAddress() {
        JsonObject result = Json.createReader(new StringReader(ClientBuilder
            .newClient()
            .target(uri)
            .path("customers")
            .path("0816")
            .request(MediaType.APPLICATION_JSON)
            .get()
            .readEntity(String.class)))
            .readObject();
        assertThat(result).is(sameAs(getClass().getResourceAsStream("erika-with-addresses.json")));
    }

    @Test
    public void setBillingAddressOfExistingCustomer() {
        Response response = ClientBuilder
            .newClient()
            .target(uri)
            .path("customers/0816/billing-address")
            .request(MediaType.APPLICATION_JSON)
            .put(entity(getClass().getResourceAsStream("sherlock-address.json"), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void setBillingAddressOfNonExistingCustomerFails() {
        Response response = ClientBuilder
            .newClient()
            .target(uri)
            .path("customers/0817/billing-address")
            .request(MediaType.APPLICATION_JSON)
            .put(entity(getClass().getResourceAsStream("sherlock-address.json"), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }


    @Test
    public void setDeliveryAddressOfExistingCustomer() {
        Response response = ClientBuilder
            .newClient()
            .target(uri)
            .path("customers/0816/delivery-address")
            .request(MediaType.APPLICATION_JSON)
            .put(entity(getClass().getResourceAsStream("sherlock-address.json"), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void setDeliveryAddressOfNonExistingCustomerFails() {
        Response response = ClientBuilder
            .newClient()
            .target(uri)
            .path("customers/0817/delivery-address")
            .request(MediaType.APPLICATION_JSON)
            .put(entity(getClass().getResourceAsStream("sherlock-address.json"), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }
}
