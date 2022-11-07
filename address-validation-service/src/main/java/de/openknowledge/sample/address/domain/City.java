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


import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.stream.Stream;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import de.openknowledge.sample.address.domain.City.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class City {

    private String name;

    public City(String name) {
        this.name = notNull(name, "name may not be empty").trim();
    }

    public City(ZipCode zipCode, CityName name) {
        this(zipCode + " " + name);
    }

    protected City() {
        // for framework
    }

    public static City valueOf(String name) {
        return new City(name);
    }

    public ZipCode getZipCode() {
        return new ZipCode(name.substring(0, 5));
    }

    public List<CityName> getCityNames() {
        String names = name.substring(5);
        if (names.endsWith("u.a.")) {
            names = names.substring(0, names.length() - "u.a.".length());
        }
        return Stream.of(names.split(",")).map(CityName::new).collect(toList());
    }

    public CityName getCityName() {
        return getCityNames().iterator().next();
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

        City city = (City)object;

        return toString().equals(city.toString());
    }

    public static class Adapter implements JsonbAdapter<City, String> {

        @Override
        public City adaptFromJson(String name) throws Exception {
            return new City(name);
        }

        @Override
        public String adaptToJson(City name) throws Exception {
            return name.toString();
        }
    }

}
