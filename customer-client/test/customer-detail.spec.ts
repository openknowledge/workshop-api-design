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
import { test, expect } from '@playwright/test';

const API_URL = 'http://localhost:8181';

const customer0815 = {
  number: '0815',
  name: 'Max Mustermann',
  billingAddress: {
    recipient: 'Max Mustermann',
    street: { name: 'Poststr.', number: '1' },
    city: '26122 Oldenburg',
  },
  deliveryAddress: {
    recipient: 'Max Mustermann',
    street: { name: 'Poststr.', number: '1' },
    city: '26122 Oldenburg',
  },
};

test.describe('Adressen anlegen', () => {
  test('Rechnungsadresse anlegen', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/007`, async (route) => {
      const customer = { number: '007', name: 'James Bond' };
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(customer),
      });
    });

    await page.route(`${API_URL}/customers/007/billing-address`, async (route) => {
      await route.fulfill({ status: 204 });
    });

    await page.goto('/customers/007');
    await expect(page.getByText('007')).toBeVisible();
    await expect(page.getByText('Name: James Bond')).toBeVisible();

    // When
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await expect(billingSection.getByText('Keine Adresse hinterlegt')).toBeVisible();
    await billingSection.getByRole('button', { name: 'Hinzufügen' }).click();
    await billingSection.getByRole('textbox', { name: 'Empfänger *' }).fill('James Bond');
    await billingSection.getByRole('textbox', { name: 'Straße' }).fill('Albert Embankment');
    await billingSection.getByRole('textbox', { name: 'Hausnummer' }).fill('85');
    await billingSection.getByRole('textbox', { name: 'PLZ / Ort' }).fill('SE1 7TP London');
    await billingSection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(billingSection.getByRole('button', { name: 'Speichern' })).not.toBeVisible();
    await expect(billingSection.getByRole('button', { name: 'Hinzufügen' })).not.toBeVisible();
  });

  test('Lieferadresse anlegen', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/007`, async (route) => {
      const customer = { number: '007', name: 'James Bond' };
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(customer),
      });
    });

    await page.route(`${API_URL}/customers/007/delivery-address`, async (route) => {
      await route.fulfill({ status: 204 });
    });

    await page.goto('/customers/007');
    await expect(page.getByText('007')).toBeVisible();
    await expect(page.getByText('Name: James Bond')).toBeVisible();

    // When
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await expect(deliverySection.getByText('Keine Adresse hinterlegt')).toBeVisible();
    await deliverySection.getByRole('button', { name: 'Hinzufügen' }).click();
    await deliverySection.getByRole('textbox', { name: 'Empfänger *' }).fill('James Bond');
    await deliverySection.getByRole('textbox', { name: 'Straße' }).fill('Chausseestraße');
    await deliverySection.getByRole('textbox', { name: 'Hausnummer' }).fill('96 - 99a');
    await deliverySection.getByRole('textbox', { name: 'PLZ' }).fill('10115');
    await deliverySection.getByRole('textbox', { name: 'Stadt' }).fill('Berlin Mitte');
    await deliverySection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(deliverySection.getByRole('button', { name: 'Speichern' })).not.toBeVisible();
    await expect(deliverySection.getByRole('button', { name: 'Hinzufügen' })).not.toBeVisible();
  });
});

test.describe('Adressen bearbeiten', () => {
  test.beforeEach(async ({ page }) => {
    await page.route(`${API_URL}/customers/0815`, async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(customer0815),
      });
    });
  });

  test('Rechnungsadresse: Straße ohne Hausnummer zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.goto('/customers/0815');
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await billingSection.getByRole('textbox', { name: 'Hausnummer' }).fill('');

    // Then
    await expect(
      billingSection.getByText('Hausnummer ist erforderlich wenn Straße angegeben'),
    ).toBeVisible();
  });

  test('Rechnungsadresse: Hausnummer ohne Straße zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.goto('/customers/0815');
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await billingSection.getByRole('textbox', { name: 'Straße' }).fill('');

    // Then
    await expect(
      billingSection.getByText('Straße ist erforderlich wenn Hausnummer angegeben'),
    ).toBeVisible();
  });

  test('Lieferadresse: Straße ohne Hausnummer zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.goto('/customers/0815');
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await deliverySection.getByRole('textbox', { name: 'Hausnummer' }).fill('');

    // Then
    await expect(
      deliverySection.getByText('Hausnummer ist erforderlich wenn Straße angegeben'),
    ).toBeVisible();
  });

  test('Lieferadresse: Hausnummer ohne Straße zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.goto('/customers/0815');
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await deliverySection.getByRole('textbox', { name: 'Straße' }).fill('');

    // Then
    await expect(
      deliverySection.getByText('Straße ist erforderlich wenn Hausnummer angegeben'),
    ).toBeVisible();
  });

  test('Lieferadresse: PLZ existiert nicht zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/0815/delivery-address`, async (route) => {
      await route.fulfill({
        status: 422,
        contentType: 'application/problem+json',
        body: JSON.stringify({ detail: 'Meinten Sie 26122 Oldenburg?' }),
      });
    });

    await page.goto('/customers/0815');
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await deliverySection.getByRole('textbox', { name: 'PLZ' }).fill('12345');
    await deliverySection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(deliverySection.getByText('Meinten Sie 26122 Oldenburg?')).toBeVisible();
  });

  test('Lieferadresse: PLZ passt nicht zum Ort zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/0815/delivery-address`, async (route) => {
      await route.fulfill({
        status: 422,
        contentType: 'application/problem+json',
        body: JSON.stringify({ detail: 'Meinten Sie 10115 Berlin Mitte?' }),
      });
    });

    await page.goto('/customers/0815');
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await deliverySection.getByRole('textbox', { name: 'PLZ' }).fill('10115');
    await deliverySection.getByRole('textbox', { name: 'Stadt' }).fill('Berlin');
    await deliverySection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(deliverySection.getByText('Meinten Sie 10115 Berlin Mitte?')).toBeVisible();
  });

  test('Lieferadresse: Unbekannte PLZ mit bekannter Stadt zeigt mögliche PLZ', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/0815/delivery-address`, async (route) => {
      await route.fulfill({
        status: 422,
        contentType: 'application/problem+json',
        body: JSON.stringify({
          detail: 'Meinten Sie eine von 30159 Hannover oder 30161 Hannover?',
        }),
      });
    });

    await page.goto('/customers/0815');
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await deliverySection.getByRole('textbox', { name: 'PLZ' }).fill('12345');
    await deliverySection.getByRole('textbox', { name: 'Stadt' }).fill('Hannover');
    await deliverySection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(deliverySection.locator('.error-message')).toContainText('Meinten Sie eine von');
    await expect(deliverySection.locator('.error-message')).toContainText('Hannover');
  });

  test('Lieferadresse: PLZ mit mehreren möglichen Orten zeigt Fehlermeldung', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/0815/delivery-address`, async (route) => {
      await route.fulfill({
        status: 422,
        contentType: 'application/problem+json',
        body: JSON.stringify({
          detail: 'Meinten Sie eine von 19322 Wittenberge oder 19322 Rühstädt?',
        }),
      });
    });

    await page.goto('/customers/0815');
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();

    // When
    await deliverySection.getByRole('textbox', { name: 'PLZ' }).fill('19322');
    await deliverySection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(deliverySection.locator('.error-message')).toContainText('Meinten Sie eine von');
    await expect(deliverySection.locator('.error-message')).toContainText('19322 Wittenberge');
    await expect(deliverySection.locator('.error-message')).toContainText('19322 Rühstädt');
  });

  test('Rechnungsadresse ändern', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/0815/billing-address`, async (route) => {
      await route.fulfill({ status: 204 });
    });

    await page.goto('/customers/0815');

    // When
    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await expect(billingSection.getByText('Empfänger: Max Mustermann')).toBeVisible();
    await expect(billingSection.getByText('Straße: Poststr. 1')).toBeVisible();
    await expect(billingSection.getByText('Ort: 26122 Oldenburg')).toBeVisible();
    await billingSection.getByRole('button', { name: 'Bearbeiten' }).click();
    await billingSection.getByRole('textbox', { name: 'Empfänger *' }).fill('James Bond');
    await billingSection.getByRole('textbox', { name: 'Straße' }).fill('Albert Embankment');
    await billingSection.getByRole('textbox', { name: 'Hausnummer' }).fill('85');
    await billingSection.getByRole('textbox', { name: 'PLZ / Ort' }).fill('SE1 7TP London');
    await billingSection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(billingSection.getByRole('button', { name: 'Speichern' })).not.toBeVisible();
    await expect(billingSection.getByRole('button', { name: 'Hinzufügen' })).not.toBeVisible();
  });

  test('Lieferadresse ändern', async ({ page }) => {
    // Given
    await page.route(`${API_URL}/customers/0815/delivery-address`, async (route) => {
      await route.fulfill({ status: 204 });
    });

    await page.goto('/customers/0815');
    await expect(page.getByText('0815')).toBeVisible();
    await expect(page.getByText('Name: Max Mustermann')).toBeVisible();

    // When
    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await expect(deliverySection.getByText('Empfänger: Max Mustermann')).toBeVisible();
    await expect(deliverySection.getByText('Straße: Poststr. 1')).toBeVisible();
    await expect(deliverySection.getByText('Ort: 26122 Oldenburg')).toBeVisible();
    await deliverySection.getByRole('button', { name: 'Bearbeiten' }).click();
    await deliverySection.getByRole('textbox', { name: 'Empfänger *' }).fill('James Bond');
    await deliverySection.getByRole('textbox', { name: 'Straße' }).fill('Chausseestraße');
    await deliverySection.getByRole('textbox', { name: 'Hausnummer' }).fill('96 - 99a');
    await deliverySection.getByRole('textbox', { name: 'PLZ' }).fill('10115');
    await deliverySection.getByRole('textbox', { name: 'Stadt' }).fill('Berlin Mitte');
    await deliverySection.getByRole('button', { name: 'Speichern' }).click();

    // Then
    await expect(deliverySection.getByRole('button', { name: 'Speichern' })).not.toBeVisible();
    await expect(deliverySection.getByRole('button', { name: 'Hinzufügen' })).not.toBeVisible();
  });
});
