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
package de.openknowledge.sample.customer.domain;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * Addresses repository
 */
@ApplicationScoped
public class CustomerRepository {

    private static final Logger LOGGER = Logger.getLogger(CustomerRepository.class.getSimpleName());
    private static final AtomicInteger CUSTOMER_NUMBERS = new AtomicInteger(0);

    private Map<CustomerNumber, Customer> customers;

    @PostConstruct
    public void initialize() {

        customers = new ConcurrentHashMap<>();
        customers.put(new CustomerNumber("0815"),
            new Customer(new CustomerNumber("0815"), new CustomerName("Max Mustermann")));
        customers.put(new CustomerNumber("0816"),
            new Customer(new CustomerNumber("0816"), new CustomerName("Erika Mustermann")));
        customers.put(new CustomerNumber("007"),
            new Customer(new CustomerNumber("007"), new CustomerName("James Bond")));
        LOGGER.info(format("customer repository initialized with %d customers: ", customers.size()));
    }

    public List<Customer> findAll() {
        return customers.values().stream().map(Customer::clearAddresses).collect(toList());
    }

    public void persist(Customer customer) {
        customer.number = new CustomerNumber(Integer.toString(CUSTOMER_NUMBERS.incrementAndGet()));
        customers.put(customer.number, customer);
    }

    public Optional<Customer> find(CustomerNumber customerNumber) {
        return Optional.ofNullable(customers.get(customerNumber));
    }
}
