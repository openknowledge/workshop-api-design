/*
 * Copyright open knowledge GmbH
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
package de.openknowledge.sample.infrastructure;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;

public class ScriptExecutor {

    private EntityManager entityManager;

    public ScriptExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static ScriptExecutor executeWith(EntityManager entityManager) {
        return new ScriptExecutor(entityManager);
    }

    public ScriptExecutor script(String name) throws IOException {
        String script = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(name));
        for (String line: script.split(";")) {
            entityManager.createNativeQuery(line).executeUpdate();
        }
        return this;
    }
}
