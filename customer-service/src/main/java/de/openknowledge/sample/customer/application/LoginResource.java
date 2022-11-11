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
package de.openknowledge.sample.customer.application;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * RESTFul endpoint for customers
 */
@ApplicationScoped
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

    @GET
    @PermitAll
    @Path("index.html")
    public String getCustomers() throws IOException {
        try (InputStream in = LoginResource.class.getResourceAsStream("index.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            return reader.lines().collect(joining(lineSeparator()));
        }
    }
}
