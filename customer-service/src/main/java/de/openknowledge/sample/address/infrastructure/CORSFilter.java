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
package de.openknowledge.sample.address.infrastructure;

import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.api.trace.Span;

import org.apache.logging.log4j.ThreadContext;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Filter to allow cross origin calls.
 */
@Provider
@ApplicationScoped
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(CORSFilter.class.getSimpleName());

    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext cres) throws IOException {
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");

        ThreadContext.remove("traceId");
        ThreadContext.remove("spanId");
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.info("Current Span: " + Span.current().getSpanContext().getTraceId() + " " + Span.current().getSpanContext().getSpanId());

        ThreadContext.put("traceId", Span.current().getSpanContext().getTraceId());
        ThreadContext.put("spanId", Span.current().getSpanContext().getSpanId());
    }
}
