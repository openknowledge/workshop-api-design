/*
 * Copyright 2023 open knowledge GmbH
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
package de.openknowledge.sample.swaggerui;

import static java.util.logging.Level.WARNING;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * RESTFul endpoint swaggerui
 */
@ApplicationScoped
@Path("{path: webjars/.*}")
public class SwaggerUiResource {

	private final static Logger LOG = Logger.getLogger(SwaggerUiResource.class.getSimpleName());

    @GET
    public Response getWebJarsResource(@PathParam("path") String path) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(String.format("META-INF/resources/%s", path));
                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
                StringWriter content = new StringWriter()) {
            buffer.lines().forEach(content::write);

            String mediaType = path.endsWith(".js") ? "application/javascript" : "text/" + path.substring(path.lastIndexOf('.') + 1);
            return Response.ok(content.toString()).type(mediaType).build();
        } catch (NullPointerException e) {
        	LOG.log(WARNING, "Could not find resource [{0}]", path);
        	return Response.status(NOT_FOUND).build();
        } catch (IOException e) {
        	LOG.severe(e.getMessage());
            return Response.status(INTERNAL_SERVER_ERROR).build();
        }
    }
    
}