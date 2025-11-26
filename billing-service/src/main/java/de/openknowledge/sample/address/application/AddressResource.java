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
package de.openknowledge.sample.address.application;

import java.util.logging.Logger;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.AddressRepository;
import de.openknowledge.sample.address.domain.CustomerNumber;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * RESTFul endpoint for delivery addresses
 */
@ApplicationScoped
@Path("/billing-addresses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {

    private static final Logger LOGGER = Logger.getLogger(AddressResource.class.getSimpleName());

    @Inject
    private AddressRepository addressesRepository;

    @GET
    @Path("/{customerNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Address getAddress(@PathParam("customerNumber") CustomerNumber number) {
        LOGGER.info("RESTful call 'GET address'");
        return addressesRepository.find(number).orElseThrow(NotFoundException::new);
    }

    @POST
    @Path("/{customerNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAddress(
        @PathParam("customerNumber") CustomerNumber customerNumber,
        Address address,
        @Context UriInfo uri) {

        LOGGER.info("RESTful call 'POST address'");
        addressesRepository.update(customerNumber, address);
        return Response.ok().build();
    }
}
