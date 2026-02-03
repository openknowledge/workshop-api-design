import { z } from 'zod';

// Zod Schemas for validation
export const streetSchema = z.object({
  name: z.string().min(1, 'Straßenname ist erforderlich'),
  number: z.string().min(1, 'Hausnummer ist erforderlich'),
});

export const citySchema = z.string().min(1, 'Stadt ist erforderlich');

export const addressSchema = z.object({
  recipient: z.string().min(1, 'Empfänger ist erforderlich'),
  street: streetSchema.optional(),
  city: citySchema.optional(),
});

export const customerNameSchema = z.string().min(1, 'Name ist erforderlich');

export const customerNumberSchema = z.string();

export const customerSchema = z.object({
  number: customerNumberSchema.optional(),
  name: customerNameSchema,
  billingAddress: addressSchema.optional(),
  deliveryAddress: addressSchema.optional(),
});

export const createCustomerSchema = z.object({
  name: customerNameSchema,
});

// TypeScript Types
export type Street = z.infer<typeof streetSchema>;
export type City = z.infer<typeof citySchema>;
export type Address = z.infer<typeof addressSchema>;
export type CustomerName = z.infer<typeof customerNameSchema>;
export type CustomerNumber = z.infer<typeof customerNumberSchema>;
export type Customer = z.infer<typeof customerSchema>;
export type CreateCustomerInput = z.infer<typeof createCustomerSchema>;
