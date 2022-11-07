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

import static java.util.stream.Collectors.joining;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.AddressRepository;
import de.openknowledge.sample.address.domain.City;

/**
 * RESTFul endpoint for valid addresses
 */
@ApplicationScoped
@Path("/valid-addresses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {

    private static final Logger LOGGER = Logger.getLogger(AddressResource.class.getSimpleName());
    private static final String PROBLEM_JSON_TYPE = "application/problem+json";
    private static final String PROBLEM_JSON
        = "{\"type\": \"%s\", \"title\": \"%s\", \"status\": %d, \"detail\": \"%s\", \"instance\": \"%s\"}";

    @Inject
    private AddressRepository addressesRepository;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateAddress(Address address, @Context UriInfo uri) throws URISyntaxException {
        LOGGER.info("RESTful call 'POST valid address'");
        if (addressesRepository.isValid(address)) {
            LOGGER.fine("address is valid");
            return Response.ok().build();
        } else {
            URI type = uri.getRequestUri().resolve("/errors/invalid-city");
            URI instance = UriBuilder.fromResource(getClass()).path(address.getCity().getZipCode().toString()).build();
            List<City> suggestions = addressesRepository.findSuggestions(address.getCity());
            LOGGER.fine(suggestions.size() + " suggestions found: " + suggestions);
            if (suggestions.size() == 1) {
                return Response.status(Response.Status.BAD_REQUEST).type(PROBLEM_JSON_TYPE)
                        .entity(String.format(PROBLEM_JSON, type, "invalid city",
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                "Did you mean " + suggestions.iterator().next() + "?", instance))
                        .build();
            } else if (!suggestions.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).type(PROBLEM_JSON_TYPE)
                        .entity(String.format(PROBLEM_JSON, type, "invalid city",
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                "Did you mean one of "
                                        + suggestions.stream().map(Object::toString).collect(joining(", ")) + "?",
                                instance))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).type(PROBLEM_JSON_TYPE)
                        .entity(String.format(PROBLEM_JSON, type, "invalid city",
                                Response.Status.BAD_REQUEST.getStatusCode(), "no matching city found", instance))
                        .build();
            }
        }
    }
}
