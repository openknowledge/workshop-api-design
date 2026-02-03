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
import type { Customer, Address, CreateCustomerInput } from '../types/customer';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

async function fetchApi<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Accept-Language': 'de',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    let errorMessage = `HTTP Error ${response.status}`;

    try {
      const errorText = await response.text();

      // Try to parse as Problem JSON
      const problemJson = JSON.parse(errorText);

      // Extract detail field if present
      if (problemJson && typeof problemJson === 'object' && 'detail' in problemJson) {
        errorMessage = problemJson.detail;
      } else {
        errorMessage = errorText;
      }
    } catch {
      // If parsing fails, keep the default error message
    }

    throw new Error(errorMessage);
  }

  if (response.status === 204 || response.status === 201) {
    return {} as T;
  }

  const text = await response.text();
  return text ? JSON.parse(text) : ({} as T);
}

export const customerApi = {
  getCustomers: async (): Promise<Customer[]> => {
    return fetchApi<Customer[]>('/customers/');
  },

  getCustomer: async (customerNumber: string): Promise<Customer> => {
    return fetchApi<Customer>(`/customers/${customerNumber}`);
  },

  createCustomer: async (customer: CreateCustomerInput): Promise<void> => {
    await fetchApi<void>('/customers/', {
      method: 'POST',
      body: JSON.stringify(customer),
    });
  },

  updateBillingAddress: async (customerNumber: string, address: Address): Promise<void> => {
    await fetchApi<void>(`/customers/${customerNumber}/billing-address`, {
      method: 'PUT',
      body: JSON.stringify(address),
    });
  },

  updateDeliveryAddress: async (customerNumber: string, address: Address): Promise<void> => {
    await fetchApi<void>(`/customers/${customerNumber}/delivery-address`, {
      method: 'PUT',
      body: JSON.stringify(address),
    });
  },
};
