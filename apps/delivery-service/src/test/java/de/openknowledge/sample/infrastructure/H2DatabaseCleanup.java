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

import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;

public class H2DatabaseCleanup {

    private EntityManager entityManager;

    public H2DatabaseCleanup(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static H2DatabaseCleanup with(EntityManager entityManager) {
        return new H2DatabaseCleanup(entityManager);
    }

    public void clearDatabase() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        Stream<Tuple> tables = (Stream<Tuple>)entityManager.createNativeQuery("SHOW TABLES", Tuple.class).getResultList().stream();
        tables.map(table -> table.get(0).toString()).forEach(table -> entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate());
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
