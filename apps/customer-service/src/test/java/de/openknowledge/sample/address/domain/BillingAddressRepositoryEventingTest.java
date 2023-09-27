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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import de.openknowledge.sample.customer.domain.CustomerNumber;

@ExtendWith(PactConsumerTestExt.class)
public class BillingAddressRepositoryEventingTest {

    private BillingAddressRepository repository;

    @Pact(consumer = "customer-message-consumer", provider = "billing-service")
    public MessagePact billingAddressOfMaxUpdated(MessagePactBuilder builder) throws IOException {
        return builder
          .given("Three customers")
            .expectsToReceive("Update for 0815")
            .withContent(new PactDslJsonBody()
                    .stringValue("number", "0815")
                    .object("billingAddress")
                        .stringValue("recipient", "Erika Mustermann")
                        .stringValue("city", "45127 Essen")
                        .object("street")
                            .stringValue("name", "II. Hagen")
                            .stringValue("number", "7")
                        .closeObject()
                    .closeObject())
            .toPact();
    }

    @BeforeEach
    public void initializeRepository() {
        repository = new BillingAddressRepository();
        repository.initializeCache();
    }

    @Test
    @PactTestFor(pactMethod = "billingAddressOfMaxUpdated", providerType = ProviderType.ASYNCH)
    public void addressUpdated(List<Message> messages) {
        
        repository.update(createEvent(messages.get(0).contentsAsBytes()));
        Optional<Address> address = repository.find(new CustomerNumber("0815"));
        assertThat(address).isPresent().contains(
                new Address(
                        new Recipient("Erika Mustermann"),
                        new Street(new StreetName("II. Hagen"), new HouseNumber("7")),
                        new City("45127 Essen")));
    }

    public InboundSseEvent createEvent(byte[] content) {
        Jsonb jsonb = JsonbBuilder.newBuilder()
                .withConfig(new JsonbConfig()
                        .withAdapters(new CustomerNumber.Adapter()))
                .build();
        InboundSseEvent event = mock(InboundSseEvent.class);
        when(event.readData()).thenReturn(new String(content));
        when(event.readData(any(Class.class)))
            .thenAnswer(i -> jsonb.fromJson(new ByteArrayInputStream(content), i.getArgument(0, Class.class)));
        when(event.readData(any(GenericType.class)))
            .thenAnswer(i -> jsonb.fromJson(new ByteArrayInputStream(content), i.getArgument(0, GenericType.class).getType()));
        when(event.readData(any(Class.class), eq(MediaType.APPLICATION_JSON_TYPE)))
            .thenAnswer(i -> jsonb.fromJson(new ByteArrayInputStream(content), i.getArgument(0, Class.class)));
        when(event.readData(any(GenericType.class), eq(MediaType.APPLICATION_JSON_TYPE)))
            .thenAnswer(i -> jsonb.fromJson(new ByteArrayInputStream(content), i.getArgument(0, GenericType.class).getType()));
        return event;
    }
}
