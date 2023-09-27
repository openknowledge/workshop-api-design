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
package de.openknowledge.sample.address.application;

import static org.apache.commons.lang3.Validate.notNull;

import javax.json.bind.annotation.JsonbProperty;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.CustomerNumber;

public class BillingAddressUpdatedEvent {

    @JsonbProperty
    private CustomerNumber number;
    @JsonbProperty
    private Address billingAddress;

    public BillingAddressUpdatedEvent(CustomerNumber number, Address billingAddress) {
        this.number = notNull(number, "customer number may not be null");
        this.billingAddress = billingAddress;
    }
}
