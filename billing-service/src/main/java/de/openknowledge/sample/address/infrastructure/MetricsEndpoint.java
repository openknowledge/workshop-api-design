/*
 * Copyright 2019 - 2025 open knowledge GmbH
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
package de.openknowledge.sample.address.infrastructure;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import org.apache.geronimo.microprofile.metrics.common.jaxrs.SecurityValidator;
import org.apache.geronimo.microprofile.metrics.jaxrs.CdiMetricsEndpoints;

@Specializes
@ApplicationScoped
public class MetricsEndpoint extends CdiMetricsEndpoints {

    private static final Logger LOG = Logger.getLogger(MetricsEndpoint.class.getSimpleName());

    @PostConstruct
    public void disableSecurity() {
        LOG.info("Unser Security Validator - postconstruct");
        setSecurityValidator(new SecurityValidator());
    }
}
