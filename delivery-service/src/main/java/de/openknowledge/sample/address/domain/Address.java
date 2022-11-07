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

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Objects;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Address {
    private Recipient recipient;
    private Street street;
    private City city;

    @JsonbCreator
    public Address(@JsonbProperty("recipient") Recipient recipient) {
        this.recipient = notNull(recipient, "recipient may not be null");
    }

    public Address(Recipient recipient, Street street, City city) {
        this(recipient);
        setStreet(street);
        setCity(city);
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public int hashCode() {
        return recipient.hashCode() + ofNullable(street).map(Object::hashCode).orElse(0) + ofNullable(city).map(Object::hashCode).orElse(0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !getClass().equals(object.getClass())) {
            return false;
        }
        Address address = (Address)object;
        return recipient.equals(recipient) && Objects.equals(street, address.street) && Objects.equals(city, address.city);
    }

    public static Builder of(String recipient) {
        return new Builder(new Recipient(recipient));
    }

    public static class Builder {

        private Address address;

        private Builder(Recipient recipient) {
            address = new Address(recipient);
        }

        public Builder atStreet(String name) {
            AddressLine addressLine = new AddressLine(name);
            address.setStreet(new Street(addressLine.getStreetName(), addressLine.getHouseNumber()));
            return this;
        }

        public Builder inCity(String city) {
            address.setCity(new City(city));
            return this;
        }

        public Address build() {
            return address;
        }
    }
}
