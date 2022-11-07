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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.openknowledge.sample.address.domain.Address;
import de.openknowledge.sample.address.domain.BillingAddressRepository;
import de.openknowledge.sample.address.domain.DeliveryAddressRepository;
import de.openknowledge.sample.customer.domain.Customer;
import de.openknowledge.sample.customer.domain.CustomerNumber;
import de.openknowledge.sample.customer.domain.CustomerRepository;

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
