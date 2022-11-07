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

import static org.apache.commons.lang3.Validate.notBlank;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import de.openknowledge.sample.customer.domain.CustomerName.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class CustomerName {

    private String name;

    public CustomerName(String name) {
        this.name = notBlank(name, "name may not be empty").trim();
    }

    protected CustomerName() {
        // for frameworks
    }

    public static CustomerName valueOf(String value) {
        return new CustomerName(value);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CustomerName)) {
            return false;
        }
        CustomerName customerName = (CustomerName)object;

        return toString().equals(customerName.toString());
    }

    public static class Adapter implements JsonbAdapter<CustomerName, String> {

        @Override
        public CustomerName adaptFromJson(String name) throws Exception {
            return new CustomerName(name);
        }

        @Override
        public String adaptToJson(CustomerName name) throws Exception {
            return name.toString();
        }
    }
}
