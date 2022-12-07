/*
 * Copyright 2019 open knowledge GmbH
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
package de.openknowledge.sample.address.domain;


import static org.apache.commons.lang3.Validate.notNull;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.STRING;

import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import de.openknowledge.sample.address.domain.City.Adapter;

@Schema(name = "City", type = STRING, example = "26122 Oldenburg")
@JsonbTypeAdapter(Adapter.class)
public class City {

    private String name;

    public static City valueOf(String name) {
        return new City(name);
    }

    public City(String name) {
        this.name = notNull(name, "name may not be empty").trim();
    }

    protected City() {
        // for framework
    }

    @Schema(hidden = true)
    public ZipCode getZipCode() {
        String firstSegment = name.substring(0, name.indexOf(' '));
        String lastSegment = name.substring(name.lastIndexOf(' ') + 1);
        if (containsDigit(firstSegment)) {
            return new ZipCode(firstSegment);
        } else if (containsDigit(lastSegment)) {
            return new ZipCode(name.substring(firstSegment.length()));
        } else {
            throw new IllegalStateException("Could not determine zip code");
        }
    }

    @Schema(hidden = true)
    public CityName getCityName() {
        String firstSegment = name.substring(0, name.indexOf(' '));
        String lastSegment = name.substring(name.lastIndexOf(' ') + 1);
        if (containsDigit(firstSegment)) {
            return new CityName(name.substring(firstSegment.length()));
        } else if (containsDigit(lastSegment)) {
            return new CityName(name.substring(0, firstSegment.length()));
        } else {
            throw new IllegalStateException("Could not determine city name");
        }
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

        City city = (City) object;

        return toString().equals(city.toString());
    }

    private boolean containsDigit(String name) {
        return name.contains("0")
                || name.contains("1")
                || name.contains("2")
                || name.contains("3")
                || name.contains("4")
                || name.contains("5")
                || name.contains("6")
                || name.contains("7")
                || name.contains("8")
                || name.contains("9");
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
