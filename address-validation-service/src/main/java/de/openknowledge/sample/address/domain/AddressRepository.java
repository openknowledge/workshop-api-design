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

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AddressRepository {

    private static final Logger LOGGER = Logger.getLogger(AddressRepository.class.getSimpleName());

    private Set<City> cities;

    @PostConstruct
    public void initialize() {
        cities = new HashSet<>();
        try (BufferedReader cityStream
            = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/plz.txt")))) {

            for (String line = cityStream.readLine(); line != null; line = cityStream.readLine()) {
                City city = new City(line);
                for (CityName name : city.getCityNames()) {
                    this.cities.add(new City(city.getZipCode(), name));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        LOGGER.info(format("address validation repository initialized with %d cities: ", cities.size()));
    }

    public boolean isValid(Address address) {
        return cities.contains(address.getCity());
    }

    public List<City> findSuggestions(City city) {
        ZipCode zipCode = city.getZipCode();
        return cities.stream().filter(c -> c.getZipCode().equals(zipCode)).collect(toList());
    }
}
