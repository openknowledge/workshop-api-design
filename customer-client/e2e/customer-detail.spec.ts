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
import { test, expect, type APIRequestContext } from '@playwright/test';

const API_URL = process.env.VITE_API_URL!;

async function createCustomer(request: APIRequestContext, name = 'Test Kunde'): Promise<string> {
  const response = await request.post(`${API_URL}/customers/`, {
    data: { name },
    headers: { 'Content-Type': 'application/json', 'Accept-Language': 'de' },
  });
  const location = response.headers()['location'];
  return location.split('/').pop()!;
}

test.describe('Kundendetails', () => {
  test('zeigt Kundennummer und Name an', async ({ page }) => {
    await page.goto('/customers/0815');

    await expect(page.getByText('0815')).toBeVisible();
    await expect(page.getByText('Name: Max Mustermann')).toBeVisible();
  });

  test('Zurück-Button navigiert zur Kundenliste', async ({ page }) => {
    await page.goto('/customers/0815');
    await page.getByRole('button', { name: /Zurück zur Übersicht/ }).click();

    await expect(page).toHaveURL('/');
  });

  test('zeigt "Keine Adresse hinterlegt" wenn keine Rechnungsadresse vorhanden', async ({ page, request }) => {
    const customerNumber = await createCustomer(request);
    await page.goto(`/customers/${customerNumber}`);

    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await expect(billingSection.getByText('Keine Adresse hinterlegt')).toBeVisible();
  });

  test('zeigt "Keine Adresse hinterlegt" wenn keine Lieferadresse vorhanden', async ({ page, request }) => {
    const customerNumber = await createCustomer(request);
    await page.goto(`/customers/${customerNumber}`);

    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await expect(deliverySection.getByText('Keine Adresse hinterlegt')).toBeVisible();
  });

  test('zeigt "Hinzufügen"-Button wenn keine Adresse vorhanden', async ({ page, request }) => {
    const customerNumber = await createCustomer(request);
    await page.goto(`/customers/${customerNumber}`);

    await expect(page.getByRole('button', { name: 'Hinzufügen' })).toHaveCount(2);
  });

  test('zeigt "Bearbeiten"-Button wenn Adresse vorhanden', async ({ page, request }) => {
    const customerNumber = await createCustomer(request);
    await request.put(`${API_URL}/customers/${customerNumber}/billing-address`, {
      data: { recipient: 'Test Empfänger', street: { name: 'Teststraße', number: '1' }, city: '26122 Oldenburg' },
      headers: { 'Content-Type': 'application/json', 'Accept-Language': 'de' },
    });
    await request.put(`${API_URL}/customers/${customerNumber}/delivery-address`, {
      data: { recipient: 'Test Empfänger', street: { name: 'Teststraße', number: '1' }, city: '26122 Oldenburg' },
      headers: { 'Content-Type': 'application/json', 'Accept-Language': 'de' },
    });
    await page.goto(`/customers/${customerNumber}`);

    await expect(page.getByRole('button', { name: 'Bearbeiten' })).toHaveCount(2);
  });

  test('zeigt Adressdaten korrekt an', async ({ page, request }) => {
    const customerNumber = await createCustomer(request);
    await request.put(`${API_URL}/customers/${customerNumber}/billing-address`, {
      data: { recipient: 'Max Mustermann', street: { name: 'Musterstraße', number: '1' }, city: '26122 Oldenburg' },
      headers: { 'Content-Type': 'application/json', 'Accept-Language': 'de' },
    });
    await request.put(`${API_URL}/customers/${customerNumber}/delivery-address`, {
      data: { recipient: 'Erika Musterfrau', street: { name: 'Beispielweg', number: '2' }, city: '26122 Oldenburg' },
      headers: { 'Content-Type': 'application/json', 'Accept-Language': 'de' },
    });
    await page.goto(`/customers/${customerNumber}`);

    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await expect(billingSection.getByText('Max Mustermann')).toBeVisible();
    await expect(billingSection.getByText(/Musterstraße 1/)).toBeVisible();
    await expect(billingSection.getByText(/26122 Oldenburg/)).toBeVisible();

    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await expect(deliverySection.getByText('Erika Musterfrau')).toBeVisible();
    await expect(deliverySection.getByText(/Beispielweg 2/)).toBeVisible();
    await expect(deliverySection.getByText(/26122 Oldenburg/)).toBeVisible();
  });
});

test.describe('Rechnungsadresse bearbeiten', () => {
  let customerNumber: string;

  test.beforeEach(async ({ request }) => {
    customerNumber = await createCustomer(request);
  });

  test('Hinzufügen-Button öffnet das Formular', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Hinzufügen' }).click();

    await expect(page.locator('#recipient')).toBeVisible();
  });

  test('Abbrechen schließt das Formular', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('button', { name: 'Abbrechen' }).click();

    await expect(page.locator('#recipient')).not.toBeVisible();
  });

  test('zeigt Fehler bei leerem Empfänger', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.locator('#recipient').fill('Max');
    await page.locator('#recipient').fill('');

    await expect(page.locator('.field-error')).toContainText('Empfänger ist erforderlich');
  });

  test('speichert Rechnungsadresse erfolgreich', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.locator('#recipient').fill('Max Mustermann');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await expect(page.locator('#recipient')).not.toBeVisible();
  });
});

test.describe('Lieferadresse bearbeiten', () => {
  let customerNumber: string;

  test.beforeEach(async ({ request }) => {
    customerNumber = await createCustomer(request);
  });

  test('zeigt Fehler bei leerer PLZ', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.locator('#zipCode').fill('12345');
    await page.locator('#zipCode').fill('');

    await expect(page.locator('.field-error')).toContainText('PLZ muss aus exakt 5 Zahlen bestehen');
  });

  test('zeigt Fehler bei ungültiger PLZ', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.locator('#zipCode').fill('abc12');

    await expect(page.locator('.field-error')).toContainText('PLZ muss aus exakt 5 Zahlen bestehen');
  });

  test('zeigt Fehler wenn PLZ und Ort nicht zusammenpassen', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.locator('#recipient').fill('Max Mustermann');
    await page.locator('#zipCode').fill('12345');
    await page.locator('#cityName').fill('Oldenburg');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await expect(page.locator('.error-message')).toBeVisible();
  });

  test('speichert Lieferadresse erfolgreich', async ({ page }) => {
    await page.goto(`/customers/${customerNumber}`);
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.locator('#recipient').fill('Max Mustermann');
    await page.locator('#zipCode').fill('26122');
    await page.locator('#cityName').fill('Oldenburg');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await expect(page.locator('#recipient')).not.toBeVisible();
  });
});
