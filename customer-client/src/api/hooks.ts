/*
 * Copyright 2026 open knowledge GmbH
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
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { customerApi } from './client';
import type { Address, CreateCustomerInput } from '../types/customer';

export const useCustomers = () => {
  return useQuery({
    queryKey: ['customers'],
    queryFn: customerApi.getCustomers,
  });
};

export const useCustomer = (customerNumber: string | undefined) => {
  return useQuery({
    queryKey: ['customer', customerNumber],
    queryFn: () => customerApi.getCustomer(customerNumber!),
    enabled: !!customerNumber,
  });
};

export const useCreateCustomer = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (customer: CreateCustomerInput) => customerApi.createCustomer(customer),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    },
  });
};

export const useUpdateBillingAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ customerNumber, address }: { customerNumber: string; address: Address }) =>
      customerApi.updateBillingAddress(customerNumber, address),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['customer', variables.customerNumber] });
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    },
  });
};

export const useUpdateDeliveryAddress = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ customerNumber, address }: { customerNumber: string; address: Address }) =>
      customerApi.updateDeliveryAddress(customerNumber, address),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['customer', variables.customerNumber] });
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    },
  });
};
