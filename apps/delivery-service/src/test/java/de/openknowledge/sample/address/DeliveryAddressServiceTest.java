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

import static de.openknowledge.sample.infrastructure.H2DatabaseCleanup.with;
import static de.openknowledge.sample.infrastructure.ScriptExecutor.executeWith;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.ValidationException;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MonoMeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import de.openknowledge.sample.address.domain.AddressValidationService;
import rocks.limburg.cdimock.MockitoBeans;

@IgnoreNoPactsToVerify
@MockitoBeans(types = {AddressValidationService.class})
@Provider("delivery-service")
@PactBroker(url = "${pactBroker.url:http://localhost}")
@MonoMeecrowaveConfig
public class DeliveryAddressServiceTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @Inject
    private AddressValidationService addressValidationService;
    @Inject
    private UserTransaction transaction;
    @Inject @Any
    private EntityManager entityManager;

    @BeforeEach
    public void setUp(PactVerificationContext context) {
        doThrow(new ValidationException("City not found")).when(addressValidationService).validate(argThat(a -> a.getCity().toString().contains("London")));
        context.setTarget(new HttpTestTarget("localhost", config.getHttpPort(), "/"));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("Three customers")
    public void setThreeCustomers() throws Exception {
        transaction.begin();
        executeWith(entityManager)
            .script("sql/create.sql")
            .script("sql/data.sql");
        transaction.commit();
    }

    @State(value = "Three customers", action = StateChangeAction.TEARDOWN)
    public void cleanupThreeCustomers() throws Exception {
        if (transaction.getStatus() != Status.STATUS_ACTIVE) {
            transaction.begin();
        }
        with(entityManager).clearDatabase();
        transaction.commit();
    }
}

