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

import de.openknowledge.sample.address.domain.ZipCode.Adapter;

@JsonbTypeAdapter(Adapter.class)
public class ZipCode {

    private String code;

    public ZipCode(String code) {
        this.code = notNull(code, "code may not be empty").trim();
    }

    public boolean isGerman() {
        return code.length() == 5;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ZipCode zipCode = (ZipCode)object;

        return toString().equals(zipCode.toString());
    }

    public static class Adapter implements JsonbAdapter<ZipCode, String> {

        @Override
        public ZipCode adaptFromJson(String zip) throws Exception {
            return new ZipCode(zip);
        }

        @Override
        public String adaptToJson(ZipCode zip) throws Exception {
            return zip.toString();
        }
    }

}
