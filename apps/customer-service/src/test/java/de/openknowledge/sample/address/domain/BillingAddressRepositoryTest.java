/*
 * Copyright open knowledge GmbH
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.client.ClientBuilder;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.johnzon.jaxrs.jsonb.jaxrs.JsonbJaxrsProvider;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import de.openknowledge.sample.customer.domain.CustomerNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "billing-service", pactVersion = PactSpecVersion.V3)
public class BillingAddressRepositoryTest {

    private BillingAddressRepository repository;

    @Pact(consumer = "customer-service")
    public RequestResponsePact getMax(PactDslWithProvider builder) throws IOException {
        return builder
          .given("Three customers")
            .uponReceiving("GET request for 0815")
            .path("/billing-addresses/0815")
            .method("GET")
          .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                    .stringValue("recipient", "Max Mustermann")
                    .stringValue("city", "26122 Oldenburg")
                    .object("street")
                        .stringValue("name", "Poststr.")
                        .stringValue("number", "1")
                    .closeObject())
          .toPact();
    }

    @Pact(consumer = "customer-service")
    public RequestResponsePact dontGetMissing(PactDslWithProvider builder) throws IOException {
        return builder
          .given("Three customers")
            .uponReceiving("GET request for 0817")
            .path("/billing-addresses/0817")
            .method("GET")
          .willRespondWith()
            .status(404)
          .toPact();
    }

    @Pact(consumer = "customer-service")
    public RequestResponsePact updateMax(PactDslWithProvider builder) throws IOException {
        return builder
          .given("Three customers")
            .uponReceiving("POST request for 0815")
            .path("/billing-addresses/0815")
            .method("POST")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
                    .stringValue("recipient", "Erika Mustermann")
                    .stringValue("city", "45127 Essen")
                    .object("street")
                        .stringValue("name", "II. Hagen")
                        .stringValue("number", "7")
                    .closeObject())
          .willRespondWith()
            .status(200)
          .toPact();
    }

    @BeforeEach
    public void initializeRepository(MockServer mockServer) {
        repository = new BillingAddressRepository();
        repository.billingServiceUrl = "http://localhost:" + mockServer.getPort();
        repository.client = ClientBuilder.newClient().register(JsonbJaxrsProvider.class);
    }

    @PactTestFor(pactMethod = "getMax")
    @Test
    public void findBillingAddressForExistingCustomer() {
        Optional<Address> address = repository.find(new CustomerNumber("0815"));
        assertThat(address).isPresent().contains(
                new Address(
                        new Recipient("Max Mustermann"),
                        new Street(new StreetName("Poststr."), new HouseNumber("1")),
                        new City("26122 Oldenburg")));
    }

    @PactTestFor(pactMethod = "dontGetMissing")
    @Test
    public void dontFindNonExistingAddress() {
        Optional<Address> address = repository.find(new CustomerNumber("0817"));
        assertThat(address).isNotPresent();
    }

    @PactTestFor(pactMethod = "updateMax")
    @Test
    public void updateAddress() {
        repository.update(new CustomerNumber("0815"), new Address(
                new Recipient("Erika Mustermann"),
                new Street(new StreetName("II. Hagen"), new HouseNumber("7")),
                new City("45127 Essen")));
    }
}
