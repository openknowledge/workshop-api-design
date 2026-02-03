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
import { useNavigate } from 'react-router-dom';
import { useForm } from '@tanstack/react-form';
import { useState } from 'react';
import { useCreateCustomer } from '../api/hooks';
import { createCustomerSchema } from '../types/customer';
import type { ZodIssue } from 'zod';

export default function CreateCustomer() {
  const navigate = useNavigate();
  const createCustomer = useCreateCustomer();
  const [submitError, setSubmitError] = useState<string | null>(null);

  const form = useForm({
    defaultValues: {
      name: '',
    },
    onSubmit: async ({ value }) => {
      try {
        setSubmitError(null);
        await createCustomer.mutateAsync({
          name: value.name,
        });
        navigate('/');
      } catch (error) {
        setSubmitError(error instanceof Error ? error.message : 'Fehler beim Erstellen des Kunden');
      }
    },
  });

  return (
    <div className="create-customer">
      <button className="button button-back" onClick={() => navigate('/')}>
        ← Zurück zur Übersicht
      </button>

      <div className="form-container">
        <h2>Neuer Kunde</h2>

        <form
          onSubmit={(e) => {
            e.preventDefault();
            e.stopPropagation();
            form.handleSubmit();
          }}
        >
          <form.Field
            name="name"
            validators={{
              onChange: createCustomerSchema.shape.name,
            }}
          >
            {(field) => (
              <div className="form-field">
                <label htmlFor="name">Name *</label>
                <input
                  id="name"
                  type="text"
                  value={field.state.value}
                  onChange={(e) => field.handleChange(e.target.value)}
                  onBlur={field.handleBlur}
                  placeholder="Kundenname"
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

          {submitError && <div className="error-message">{submitError}</div>}

          <div className="form-actions">
            <button type="button" className="button" onClick={() => navigate('/')}>
              Abbrechen
            </button>
            <button
              type="submit"
              className="button button-primary"
              disabled={createCustomer.isPending}
            >
              {createCustomer.isPending ? 'Erstelle...' : 'Kunde erstellen'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
