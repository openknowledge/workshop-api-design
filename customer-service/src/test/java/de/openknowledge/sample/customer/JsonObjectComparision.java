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
package de.openknowledge.sample.customer;

import java.io.InputStream;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.assertj.core.api.Condition;

public class JsonObjectComparision extends Condition<Map<String, JsonValue>> {

    public JsonObjectComparision(JsonObject object) {
        super(v -> v.entrySet().containsAll(object.entrySet()), "object containing %s", object);
    }

    public static Condition<Map<String, JsonValue>> sameAs(InputStream in) {
        return new JsonObjectComparision(Json.createReader(in).readObject());
    }

    public static Condition<JsonValue> thatIsSameAs(InputStream in) {
        Condition<? super JsonObject> condition = new JsonObjectComparision(Json.createReader(in).readObject());
        return (Condition<JsonValue>)condition;
    }
}
