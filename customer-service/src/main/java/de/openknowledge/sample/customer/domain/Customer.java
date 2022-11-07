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

import static org.apache.commons.lang3.Validate.notNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import de.openknowledge.sample.address.domain.Address;

public class Customer {

    CustomerNumber number;
    private CustomerName name;
    private Address billingAddress;
    private Address deliveryAddress;

    @JsonbCreator
    public Customer(@JsonbProperty("name") CustomerName name) {
        this.name = notNull(name, "name may not be null");
    }

    public Customer(CustomerNumber number, CustomerName name) {
        this.name = notNull(name, "name may not be null");
        this.number = notNull(number, "number may not be null");
    }

    public CustomerName getName() {
        return name;
    }

    public CustomerNumber getNumber() {
        return number;
    }

    @JsonbProperty(nillable = false)
    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    @JsonbProperty(nillable = false)
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    Customer clearAddresses() {
        deliveryAddress = null;
        billingAddress = null;
        return this;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ number.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Customer)) {
            return false;
        }

        Customer customer = (Customer)object;

        return name.equals(customer.getName()) && number.equals(customer.getNumber());
    }

    @Override
    public String toString() {
        return name + "(" + number + ")";
    }
}
