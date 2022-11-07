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

import de.openknowledge.sample.address.domain.City.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class City {

    private String name;

    public City(String name) {
        this.name = notNull(name, "name may not be empty").trim();
    }

    protected City() {
        // for framework
    }

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

        City city = (City)object;

        return toString().equals(city.toString());
    }

    private boolean containsDigit(String cityName) {
        return cityName.contains("0")
            || cityName.contains("1")
            || cityName.contains("2")
            || cityName.contains("3")
            || cityName.contains("4")
            || cityName.contains("5")
            || cityName.contains("6")
            || cityName.contains("7")
            || cityName.contains("8")
            || cityName.contains("9");
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
