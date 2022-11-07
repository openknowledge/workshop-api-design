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

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Location {

    private ZipCode zipCode;
    private CityName cityName;

    @JsonbCreator
    public Location(@JsonbProperty("zipCode") ZipCode zipCode, @JsonbProperty("cityName") CityName city) {
        this.zipCode = notNull(zipCode, "zip code may not be null");
        this.cityName = notNull(city, "city name may not be null");
    }

    public ZipCode getZipCode() {
        return zipCode;
    }

    public CityName getCityName() {
        return cityName;
    }

    @Override
    public int hashCode() {
        return zipCode.hashCode() ^ cityName.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Location)) {
            return false;
        }

        Location location = (Location)object;

        return zipCode.equals(location.zipCode) && cityName.equals(location.cityName);
    }

    @Override
    public String toString() {
        return zipCode.isGerman() ? zipCode + " " + cityName : cityName + " " + zipCode;
    }
}
