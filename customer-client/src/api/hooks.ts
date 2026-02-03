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
