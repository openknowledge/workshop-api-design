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

import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MonoMeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.BillingAddressRepository;
import de.openknowledge.sample.address.domain.DeliveryAddressRepository;
import de.openknowledge.sample.customer.domain.CustomerNumber;
import rocks.limburg.cdimock.MockitoBeans;

@Provider("customer-service")
@PactFolder("../pacts")
@MockitoBeans(types = {BillingAddressRepository.class, DeliveryAddressRepository.class})
@MonoMeecrowaveConfig
public class CustomerServiceTest {

    @ConfigurationInject
    private Meecrowave.Builder config;

    @Inject
    private BillingAddressRepository billingAddressRepository;

    @Inject
    private DeliveryAddressRepository deliveryAddressRepository;

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

        doThrow(new ValidationException("Meinten Sie 26122 Oldenburg?"))
            .when(deliveryAddressRepository).update(any(CustomerNumber.class),
            argThat(a -> "12345 Oldenburg".equals(a.getCity().toString())));
        doThrow(new ValidationException("Meinten Sie 10115 Berlin Mitte?"))
            .when(deliveryAddressRepository).update(any(CustomerNumber.class),
            argThat(a -> "10115 Berlin".equals(a.getCity().toString())));
        doThrow(new ValidationException("Meinten Sie eine von 30159 Hannover oder 30161 Hannover?"))
            .when(deliveryAddressRepository).update(any(CustomerNumber.class),
            argThat(a -> "12345 Hannover".equals(a.getCity().toString())));
        doThrow(new ValidationException("Meinten Sie eine von 19322 Wittenberge oder 19322 Rühstädt?"))
            .when(deliveryAddressRepository).update(any(CustomerNumber.class),
            argThat(a -> "19322 Oldenburg".equals(a.getCity().toString())));
    }

    @BeforeEach
    public void setUp(PactVerificationContext verificationContext) {
        ofNullable(verificationContext)
            .ifPresent(context -> context.setTarget(new HttpTestTarget("localhost", config.getHttpPort(), "/")));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        ofNullable(context).ifPresent(PactVerificationContext::verifyInteraction);
    }
}
