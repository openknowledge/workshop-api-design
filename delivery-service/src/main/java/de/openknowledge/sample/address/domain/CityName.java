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

import static org.apache.commons.lang3.Validate.notNull;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import de.openknowledge.sample.address.domain.CityName.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class CityName {

    private String name;

    public CityName(String name) {
        this.name = notNull(name, "name may not be empty").trim();
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

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        CityName city = (CityName)object;

        return toString().equals(city.toString());
    }

    public static class Adapter implements JsonbAdapter<CityName, String> {

        @Override
        public CityName adaptFromJson(String name) throws Exception {
            return new CityName(name);
        }

        @Override
        public String adaptToJson(CityName name) throws Exception {
            return name.toString();
        }
    }

}
