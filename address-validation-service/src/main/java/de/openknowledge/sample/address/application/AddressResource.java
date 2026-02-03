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

import static jakarta.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static java.util.Locale.GERMANY;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

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

    @GET
    public Response healthCheck() {
        return Response.ok().build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateAddress(Address address, @Context UriInfo uri, @HeaderParam(ACCEPT_LANGUAGE) Locale locale) {
        LOGGER.info("RESTful call 'POST valid address'");
        if (addressesRepository.isValid(address)) {
            return Response.ok().build();
        } else {
            URI type = uri.getRequestUri().resolve("/errors/invalid-city");
            URI instance = UriBuilder.fromResource(getClass()).path(address.getCity().getZipCode().toString()).build();
            List<City> suggestions = addressesRepository.findSuggestions(address.getCity());
            LOGGER.fine(suggestions.size() + " suggestions found: " + suggestions);
            if (suggestions.size() == 1) {
                return Response.status(Response.Status.BAD_REQUEST).type(PROBLEM_JSON_TYPE)
                        .entity(String.format(PROBLEM_JSON, type,
                                translate("invalid city", locale),
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                translate("Did you mean %s?", locale, suggestions.iterator().next()),
                                instance))
                        .build();
            } else if (!suggestions.isEmpty()) {
                String suggestionList = suggestions.stream().map(Object::toString).collect(joining(", "));
                return Response.status(Response.Status.BAD_REQUEST).type(PROBLEM_JSON_TYPE)
                        .entity(String.format(PROBLEM_JSON, type,
                                translate("invalid city", locale),
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                translate("Did you mean one of %s?", locale, suggestionList),
                                instance))
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).type(PROBLEM_JSON_TYPE)
                        .entity(String.format(PROBLEM_JSON, type,
                                translate("invalid city", locale),
                                Response.Status.BAD_REQUEST.getStatusCode(),
                                translate("no matching city found",
                                locale),
                                instance))
                        .build();
            }
        }
    }

    private String translate(String template, Locale language, Object... parameters) {
        if (ofNullable(language).map(Locale::getLanguage).filter(GERMANY.getLanguage()::equals).isPresent()) {
            switch (template) {
                case "Did you mean %s?":
                    return "Meinten Sie %s?".formatted(parameters);
                case "invalid city":
                    return "ungültige Stadt";
                case "no matching city found":
                    return "keine passende Stadt gefunden";
                case "Did you mean one of %s?":
                    return "Meinten Sie eine von %s?".formatted(parameters);
                default:
                    return template.formatted(parameters);
            }
        }
        return template.formatted(parameters);
    }
}
