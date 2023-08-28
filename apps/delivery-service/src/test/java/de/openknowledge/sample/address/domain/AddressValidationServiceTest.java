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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "address-validation-service", pactVersion = PactSpecVersion.V3)
public class AddressValidationServiceTest {

    private AddressValidationService service;

    @Pact(consumer = "delivery-service")
    public RequestResponsePact validateMaxsAddress(PactDslWithProvider builder) throws IOException {
        return builder
          .given("Three customers")
            .uponReceiving("POST request for Max's address")
            .path("/valid-addresses")
            .method("POST")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
              .stringValue("recipient", "Max Mustermann")
              .stringValue("city", "26122 Oldenburg")
              .object("street")
                .stringValue("name", "Poststrasse")
                .stringValue("number", "1")
              .closeObject())
            .willRespondWith()
              .status(200)
            .toPact();
    }

    @Pact(consumer = "delivery-service")
    public RequestResponsePact validateSherlocksAddress(PactDslWithProvider builder) throws IOException {
        return builder
          .given("Three customers")
            .uponReceiving("POST request for Sherlock's address")
            .path("/valid-addresses")
            .method("POST")
            .matchHeader("Content-Type", "application/json.*", "application/json")
            .body(new PactDslJsonBody()
                    .stringValue("recipient", "Sherlock Holmes")
                    .stringValue("city", "London NW1 6XE")
                    .object("street")
                        .stringValue("name", "Baker Street")
                        .stringValue("number", "221B")
                    .closeObject())
          .willRespondWith()
            .status(400)
            .matchHeader("Content-Type", "application/problem\\+json.*", "application/problem+json")
            .body(new PactDslJsonBody().stringMatcher("detail", ".*", "Addresses from UK are not supported for delivery"))
          .toPact();
    }

    @BeforeEach
    public void initializeService(MockServer mockServer) {
        service = new AddressValidationService();
        service.addressValidationServiceUrl = "http://localhost:" + mockServer.getPort();
    }

    @PactTestFor(pactMethod = "validateMaxsAddress")
    @Test
    public void validAddress() {
        service.validate(new Address(
                            new Recipient("Max Mustermann"),
                            new Street(new StreetName("Poststrasse"), new HouseNumber("1")),
                            new City("26122 Oldenburg")));
    }

    @Test
    @PactTestFor(pactMethod = "validateSherlocksAddress")
    public void invalidAddress() {
        assertThatThrownBy(() -> 
            service.validate(new Address(
                    new Recipient("Sherlock Holmes"),
                    new Street(new StreetName("Baker Street"), new HouseNumber("221B")),
                    new City("London NW1 6XE"))))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Addresses from UK are not supported for delivery");
    }
}
