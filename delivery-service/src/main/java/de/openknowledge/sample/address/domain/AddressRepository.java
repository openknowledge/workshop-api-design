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

import static java.lang.String.format;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * Addresses repository
 */
@ApplicationScoped
public class AddressRepository {

    private static final Logger LOGGER = Logger.getLogger(AddressRepository.class.getSimpleName());

    private Map<CustomerNumber, Address> addresses;

    @PostConstruct
    public void initialize() {

        addresses = new ConcurrentHashMap<>();

        addresses.put(new CustomerNumber("0815"), new Address(new Recipient("Max Mustermann"),
            new Street(new StreetName("Poststr."), new HouseNumber("1")), new City("26122 Oldenburg")));

        addresses.put(new CustomerNumber("0816"), new Address(new Recipient("Erika Mustermann"),
            new Street(new StreetName("II. Hagen"), new HouseNumber("7")), new City("45127 Essen")));
        LOGGER.info(format("address repository initialized with %d addresses: ", addresses.size()));
    }

    public Optional<Address> find(CustomerNumber number) {
        return Optional.ofNullable(addresses.get(number));
    }

    public void update(CustomerNumber number, Address address) {
        Optional.ofNullable(address).ifPresent(a -> addresses.put(number, a));
    }
}
