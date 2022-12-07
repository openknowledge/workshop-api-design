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

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Street")
public class Street {

    private StreetName name;
    private HouseNumber number;

    @JsonbCreator
    public Street(@JsonbProperty("name") StreetName name, @JsonbProperty("number") HouseNumber houseNumber) {
        this.name = notNull(name, "name may not be null");
        this.number = notNull(houseNumber, "house number may not be null");
    }

    @Schema(name = "name", type = STRING, example = "Poststr.")
    public StreetName getName() {
        return name;
    }

    @Schema(name = "number", type = STRING, example = "1")
    public HouseNumber getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ number.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Street)) {
            return false;
        }

        Street street = (Street) object;

        return name.equals(street.getName()) && number.equals(street.getNumber());
    }

    @Override
    public String toString() {
        if (isEnglish()) {
            return number + " " + name;
        } else {
            return name + " " + number;
        }
    }

    private boolean isEnglish() {
        return name.toString().toLowerCase().contains("street");
    }
}
