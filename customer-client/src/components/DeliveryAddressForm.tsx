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
import { useForm } from '@tanstack/react-form';
import { useState } from 'react';
import { useUpdateDeliveryAddress } from '../api/hooks';
import { addressSchema } from '../types/customer';
import type { Address } from '../types/customer';
import { z, type ZodIssue } from 'zod';

interface DeliveryAddressFormProps {
  customerNumber: string;
  initialAddress?: Address;
  onSuccess: () => void;
  onCancel: () => void;
}

export default function DeliveryAddressForm({
  customerNumber,
  initialAddress,
  onSuccess,
  onCancel,
}: DeliveryAddressFormProps) {
  const updateDeliveryAddress = useUpdateDeliveryAddress();
  const [submitError, setSubmitError] = useState<string | null>(null);

  const form = useForm({
    defaultValues: {
      recipient: initialAddress?.recipient || '',
      streetName: initialAddress?.street?.name || '',
      houseNumber: initialAddress?.street?.number || '',
      zipCode: initialAddress?.city?.split(' ')[0] || '',
      cityName: initialAddress?.city?.split(' ').slice(1).join(' ') || '',
    },
    onSubmit: async ({ value }) => {
      try {
        setSubmitError(null);
        const address: Address = {
          recipient: value.recipient,
          street:
            value.streetName || value.houseNumber
              ? {
                  name: value.streetName,
                  number: value.houseNumber,
                }
              : undefined,
          city: value.zipCode && value.cityName ? `${value.zipCode} ${value.cityName}` : undefined,
        };
        await updateDeliveryAddress.mutateAsync({ customerNumber, address });
        onSuccess();
      } catch (error) {
        setSubmitError(
          error instanceof Error ? error.message : 'Fehler beim Speichern der Adresse',
        );
      }
    },
  });

  return (
    <form
      className="address-form"
      onSubmit={(e) => {
        e.preventDefault();
        e.stopPropagation();
        form.handleSubmit();
      }}
    >
      <form.Field
        name="recipient"
        validators={{
          onChange: addressSchema.shape.recipient,
        }}
      >
        {(field) => (
          <div className="form-field">
            <label htmlFor="recipient">Empfänger *</label>
            <input
              id="recipient"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              placeholder="Name des Empfängers"
              className={field.state.meta.errors.length > 0 ? 'input-error' : ''}
            />
            {field.state.meta.errors.length > 0 && (
              <span className="field-error">
                {typeof field.state.meta.errors[0] === 'string'
                  ? field.state.meta.errors[0]
                  : (field.state.meta.errors[0] as ZodIssue).message}
              </span>
            )}
          </div>
        )}
      </form.Field>

      <form.Field
        name="streetName"
        validators={{
          onChangeListenTo: ['houseNumber'],
          onChange: ({ value, fieldApi }) => {
            const houseNumber = fieldApi.form.getFieldValue('houseNumber');
            return z
              .string()
              .refine(
                (v) => !houseNumber || v.length > 0,
                'Straße ist erforderlich wenn Hausnummer angegeben',
              )
              .safeParse(value).error?.issues[0];
          },
        }}
      >
        {(field) => (
          <div className="form-field">
            <label htmlFor="streetName">Straße</label>
            <input
              id="streetName"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              placeholder="Straßenname"
              className={field.state.meta.errors.length > 0 ? 'input-error' : ''}
            />
            {field.state.meta.errors.length > 0 && (
              <span className="field-error">
                {typeof field.state.meta.errors[0] === 'string'
                  ? field.state.meta.errors[0]
                  : (field.state.meta.errors[0] as ZodIssue).message}
              </span>
            )}
          </div>
        )}
      </form.Field>

      <form.Field
        name="houseNumber"
        validators={{
          onChangeListenTo: ['streetName'],
          onChange: ({ value, fieldApi }) => {
            const streetName = fieldApi.form.getFieldValue('streetName');
            return z
              .string()
              .refine(
                (v) => !streetName || v.length > 0,
                'Hausnummer ist erforderlich wenn Straße angegeben',
              )
              .safeParse(value).error?.issues[0];
          },
        }}
      >
        {(field) => (
          <div className="form-field">
            <label htmlFor="houseNumber">Hausnummer</label>
            <input
              id="houseNumber"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              placeholder="Hausnummer"
              className={field.state.meta.errors.length > 0 ? 'input-error' : ''}
            />
            {field.state.meta.errors.length > 0 && (
              <span className="field-error">
                {typeof field.state.meta.errors[0] === 'string'
                  ? field.state.meta.errors[0]
                  : (field.state.meta.errors[0] as ZodIssue).message}
              </span>
            )}
          </div>
        )}
      </form.Field>

      <form.Field
        name="zipCode"
        validators={{
          onChange: z
            .string()
            .regex(/^\d{5}$/, 'PLZ muss aus exakt 5 Zahlen bestehen')
            .or(z.string().length(0)),
        }}
      >
        {(field) => (
          <div className="form-field">
            <label htmlFor="zipCode">PLZ *</label>
            <input
              id="zipCode"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              onBlur={field.handleBlur}
              placeholder="Postleitzahl"
              className={field.state.meta.errors.length > 0 ? 'input-error' : ''}
            />
            {field.state.meta.errors.length > 0 && (
              <span className="field-error">
                {typeof field.state.meta.errors[0] === 'string'
                  ? field.state.meta.errors[0]
                  : (field.state.meta.errors[0] as ZodIssue).message}
              </span>
            )}
          </div>
        )}
      </form.Field>

      <form.Field name="cityName">
        {(field) => (
          <div className="form-field">
            <label htmlFor="cityName">Stadt</label>
            <input
              id="cityName"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              placeholder="Stadtname"
            />
          </div>
        )}
      </form.Field>

      {submitError && <div className="error-message">{submitError}</div>}

      <div className="form-actions">
        <button type="button" className="button" onClick={onCancel}>
          Abbrechen
        </button>
        <button
          type="submit"
          className="button button-primary"
          disabled={updateDeliveryAddress.isPending}
        >
          {updateDeliveryAddress.isPending ? 'Speichere...' : 'Speichern'}
        </button>
      </div>
    </form>
  );
}
