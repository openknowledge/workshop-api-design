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

import static org.apache.commons.lang3.Validate.notBlank;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import de.openknowledge.sample.address.domain.CustomerNumber.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class CustomerNumber {

    private String number;

    protected CustomerNumber() {
        // for frameworks
    }

    public CustomerNumber(String number) {
        this.number = notBlank(number, "number may not be empty").trim();
    }

    @Override
    public String toString() {
        return number;
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CustomerNumber)) {
            return false;
        }

        CustomerNumber customerNumber = (CustomerNumber)object;

        return toString().equals(customerNumber.toString());
    }

    public static class Adapter implements JsonbAdapter<CustomerNumber, String> {

        @Override
        public CustomerNumber adaptFromJson(String number) throws Exception {
            return new CustomerNumber(number);
        }

        @Override
        public String adaptToJson(CustomerNumber number) throws Exception {
            return number.toString();
        }

    }

}
