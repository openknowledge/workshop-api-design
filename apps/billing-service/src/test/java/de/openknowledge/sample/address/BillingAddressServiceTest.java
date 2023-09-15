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

import static java.util.Optional.ofNullable;

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
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;

@IgnoreNoPactsToVerify
@Provider("billing-service")
@PactBroker(url = "${pactBroker.url:http://localhost}", consumerVersionSelectors = @VersionSelector(tag = "${pact.tag}"))
@MonoMeecrowaveConfig
public class BillingAddressServiceTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @BeforeEach
    public void setUp(PactVerificationContext context) {
        ofNullable(context).ifPresent(c -> c.setTarget(new HttpTestTarget("localhost", config.getHttpPort(), "/")));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        ofNullable(context).ifPresent(PactVerificationContext::verifyInteraction);
    }

    @State("Three customers")
    public void toDefaultState() {
        System.out.println("Now service in default state");
    }
}
