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

import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.BillingAddressRepository;
import de.openknowledge.sample.address.domain.DeliveryAddressRepository;
import de.openknowledge.sample.customer.domain.Customer;
import de.openknowledge.sample.customer.domain.CustomerNumber;
import de.openknowledge.sample.customer.domain.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

/**
 * RESTFul endpoint for customers
 */
@ApplicationScoped
@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private static final Logger LOG = Logger.getLogger(CustomerResource.class.getSimpleName());

    @Inject
    private CustomerRepository customerRepository;
    @Inject
    private BillingAddressRepository billingAddressRepository;
    @Inject
    private DeliveryAddressRepository deliveryAddressRepository;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getCustomers() {
        LOG.info("RESTful call 'GET all customers'");
        return customerRepository.findAll();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer customer, @Context UriInfo uri) throws URISyntaxException {
        LOG.info("RESTful call 'POST new customer'");
        customerRepository.persist(customer);
        return Response.created(uri.getAbsolutePathBuilder().path(customer.getNumber().toString()).build()).build();
    }

    @GET
    @Path("/{customerNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomer(@PathParam("customerNumber") CustomerNumber customerNumber) {
        LOG.info("RESTful call 'GET customer'");
        Customer customer = customerRepository.find(customerNumber).orElseThrow(customerNotFound(customerNumber));
        billingAddressRepository.find(customerNumber).ifPresent(customer::setBillingAddress);
        deliveryAddressRepository.find(customerNumber).ifPresent(customer::setDeliveryAddress);
        return customer;
    }

    @PUT
    @Path("/{customerNumber}/billing-address")
    @Produces(MediaType.APPLICATION_JSON)
    public void setBillingAddress(@PathParam("customerNumber") CustomerNumber customerNumber, Address billingAddress) {
        LOG.info("RESTful call 'PUT billing address'");
        customerRepository.find(customerNumber).orElseThrow(customerNotFound(customerNumber));
        billingAddressRepository.update(customerNumber, billingAddress);
    }

    @PUT
    @Path("/{customerNumber}/delivery-address")
    @Produces(MediaType.APPLICATION_JSON)
    public void setDeliveryAddress(
        @PathParam("customerNumber") CustomerNumber customerNumber,
        Address deliveryAddress) {

        LOG.info("RESTful call 'PUT delivery address'");
        customerRepository.find(customerNumber).orElseThrow(customerNotFound(customerNumber));
        deliveryAddressRepository.update(customerNumber, deliveryAddress);
    }

    private Supplier<NotFoundException> customerNotFound(CustomerNumber number) {
        return () -> new NotFoundException("customer " + number + " not found");
    }
}
