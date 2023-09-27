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
package de.openknowledge.sample.address;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

@Provider("billing-service")
@PactFolder("src/test/pacts")
@MeecrowaveConfig
public class BillingAddressServiceTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @BeforeEach
    public void setUp(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @PactVerifyProvider("Update for 0815")
    public String updateBillingAddress() throws InterruptedException, ExecutionException, TimeoutException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:" + config.getActivePort() + "/billing-addresses/0815");
        CompletableFuture<InboundSseEvent> futureEvent = new CompletableFuture<>();
        SseEventSource source = SseEventSource.target(target).build();
        source.register((sseEvent) -> futureEvent.complete(sseEvent));
        source.open();
        target
            .request(APPLICATION_JSON)
            .post(entity(getClass().getResourceAsStream("0816.json"), APPLICATION_JSON_TYPE));
        
        return futureEvent.get().readData();
    }

    @State("Three customers")
    public void toDefaultState() {
        System.out.println("Now service in default state");
    }
}
