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
import { useParams, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useCustomer } from '../api/hooks';
import BillingAddressForm from '../components/BillingAddressForm';
import DeliveryAddressForm from '../components/DeliveryAddressForm';
import type { Address } from '../types/customer';

export default function CustomerDetail() {
  const { customerNumber } = useParams<{ customerNumber: string }>();
  const navigate = useNavigate();
  const { data: customer, isLoading, error } = useCustomer(customerNumber);
  const [editingBilling, setEditingBilling] = useState(false);
  const [editingDelivery, setEditingDelivery] = useState(false);

  if (isLoading) {
    return (
      <div className="loading">
        <p>Lade Kundendetails...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error">
        <p>Fehler beim Laden des Kunden: {error.message}</p>
        <button className="button" onClick={() => navigate('/')}>
          Zurück zur Übersicht
        </button>
      </div>
    );
  }

  if (!customer) {
    return (
      <div className="error">
        <p>Kunde nicht gefunden</p>
        <button className="button" onClick={() => navigate('/')}>
          Zurück zur Übersicht
        </button>
      </div>
    );
  }

  const renderAddress = (address: Address | undefined) => {
    if (!address) {
      return <p className="no-address">Keine Adresse hinterlegt</p>;
    }

    return (
      <div className="address-display">
        <p>
          <strong>Empfänger:</strong> {address.recipient}
        </p>
        {address.street && (
          <p>
            <strong>Straße:</strong> {address.street.name} {address.street.number}
          </p>
        )}
        {address.city && (
          <p>
            <strong>Ort:</strong> {address.city}
          </p>
        )}
      </div>
    );
  };

  return (
    <div className="customer-detail">
      <button className="button button-back" onClick={() => navigate('/')}>
        ← Zurück zur Übersicht
      </button>

      <div className="customer-detail-header">
        <h2>Kundendetails</h2>
      </div>

      <div className="customer-info">
        <div className="info-row">
          <strong>Kundennummer:</strong> {customer.number}
        </div>
        <div className="info-row">
          <strong>Name:</strong> {customer.name}
        </div>
      </div>

      <div className="addresses">
        <div className="address-section">
          <div className="address-section-header">
            <h3>Rechnungsadresse</h3>
            {!editingBilling && (
              <button className="button button-small" onClick={() => setEditingBilling(true)}>
                {customer.billingAddress ? 'Bearbeiten' : 'Hinzufügen'}
              </button>
            )}
          </div>
          {editingBilling ? (
            <BillingAddressForm
              customerNumber={customerNumber!}
              initialAddress={customer.billingAddress}
              onSuccess={() => setEditingBilling(false)}
              onCancel={() => setEditingBilling(false)}
            />
          ) : (
            renderAddress(customer.billingAddress)
          )}
        </div>

        <div className="address-section">
          <div className="address-section-header">
            <h3>Lieferadresse</h3>
            {!editingDelivery && (
              <button className="button button-small" onClick={() => setEditingDelivery(true)}>
                {customer.deliveryAddress ? 'Bearbeiten' : 'Hinzufügen'}
              </button>
            )}
          </div>
          {editingDelivery ? (
            <DeliveryAddressForm
              customerNumber={customerNumber!}
              initialAddress={customer.deliveryAddress}
              onSuccess={() => setEditingDelivery(false)}
              onCancel={() => setEditingDelivery(false)}
            />
          ) : (
            renderAddress(customer.deliveryAddress)
          )}
        </div>
      </div>
    </div>
  );
}
