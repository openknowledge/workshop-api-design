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

import de.openknowledge.sample.address.domain.AddressLine.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class AddressLine {

    public static final AddressLine EMPTY = new AddressLine("");

    private String line;

    protected AddressLine() {
        // for frameworks
    }

    public AddressLine(String line) {
        this.line = notNull(line, "line may not be null").trim();
    }

    public StreetName getStreetName() {
        String firstSegment = line.substring(0, line.indexOf(' '));
        String lastSegment = line.substring(line.lastIndexOf(' ') + 1);
        if (containsDigit(lastSegment)) {
            return new StreetName(line.substring(0, line.length() - lastSegment.length()));
        } else if (containsDigit(firstSegment)) {
            return new StreetName(line.substring(firstSegment.length()));
        } else {
            throw new IllegalStateException("Could not determine street name");
        }
    }

    public HouseNumber getHouseNumber() {
        String firstSegment = line.substring(0, line.indexOf(' '));
        String lastSegment = line.substring(line.lastIndexOf(' ') + 1);
        if (containsDigit(lastSegment)) {
            return new HouseNumber(lastSegment);
        } else if (containsDigit(firstSegment)) {
            return new HouseNumber(firstSegment);
        } else {
            throw new IllegalStateException("Could not determine house number");
        }
    }

    @Override
    public String toString() {
        return line;
    }


    @Override
    public int hashCode() {
        return line.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof AddressLine)) {
            return false;
        }

        AddressLine recipient = (AddressLine)object;

        return toString().equals(recipient.toString());
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

    public static class Adapter implements JsonbAdapter<AddressLine, String> {

        @Override
        public AddressLine adaptFromJson(String name) throws Exception {
            return new AddressLine(name);
        }

        @Override
        public String adaptToJson(AddressLine name) throws Exception {
            return name.toString();
        }
    }
}
