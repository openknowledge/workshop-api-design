import { useForm } from '@tanstack/react-form';
import { useState } from 'react';
import { useUpdateDeliveryAddress } from '../api/hooks';
import { addressSchema } from '../types/customer';
import type { Address } from '../types/customer';
import { z } from 'zod';

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
      zipCode: '',
      cityName: '',
    },
    onSubmit: async ({ value }) => {
      try {
        setSubmitError(null);
        const address: Address = {
          recipient: value.recipient,
          street:
            value.streetName && value.houseNumber
              ? {
                  name: value.streetName,
                  number: value.houseNumber,
                }
              : undefined,
          city:
            value.zipCode && value.cityName
              ? `${value.zipCode} ${value.cityName}`
              : undefined,
        };
        await updateDeliveryAddress.mutateAsync({ customerNumber, address });
        onSuccess();
      } catch (error) {
        setSubmitError(
          error instanceof Error ? error.message : 'Fehler beim Speichern der Adresse'
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
                  : (field.state.meta.errors[0] as any)?.message || String(field.state.meta.errors[0])}
              </span>
            )}
          </div>
        )}
      </form.Field>

      <form.Field name="streetName">
        {(field) => (
          <div className="form-field">
            <label htmlFor="streetName">Straße</label>
            <input
              id="streetName"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              placeholder="Straßenname"
            />
          </div>
        )}
      </form.Field>

      <form.Field name="houseNumber">
        {(field) => (
          <div className="form-field">
            <label htmlFor="houseNumber">Hausnummer</label>
            <input
              id="houseNumber"
              type="text"
              value={field.state.value}
              onChange={(e) => field.handleChange(e.target.value)}
              placeholder="Hausnummer"
            />
          </div>
        )}
      </form.Field>

      <form.Field
        name="zipCode"
        validators={{
          onChange: z.string().regex(/^\d{5}$/, 'PLZ muss aus exakt 5 Zahlen bestehen').or(z.string().length(0)),
        }}
      >
        {(field) => (
          <div className="form-field">
            <label htmlFor="zipCode">PLZ</label>
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
                  : (field.state.meta.errors[0] as any)?.message || String(field.state.meta.errors[0])}
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
