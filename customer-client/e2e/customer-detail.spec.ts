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

const API_URL = process.env.VITE_API_URL!;
const CUSTOMER_NUMBER = '0815';
const CUSTOMER_URL = `/customers/${CUSTOMER_NUMBER}`;

const customerWithoutAddresses = {
  number: CUSTOMER_NUMBER,
  name: 'Max Mustermann',
};

const customerWithAddresses = {
  number: CUSTOMER_NUMBER,
  name: 'Max Mustermann',
  billingAddress: {
    recipient: 'Max Mustermann',
    street: { name: 'Musterstraße', number: '1' },
    city: '12345 Musterstadt',
  },
  deliveryAddress: {
    recipient: 'Erika Musterfrau',
    street: { name: 'Beispielweg', number: '2' },
    city: '54321 Beispielstadt',
  },
};

test.describe('Kundendetails', () => {
  test('zeigt Kundennummer und Name an', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });

    await page.goto(CUSTOMER_URL);

    await expect(page.getByText(CUSTOMER_NUMBER)).toBeVisible();
    await expect(page.getByText('Max Mustermann')).toBeVisible();
  });

  test('Zurück-Button navigiert zur Kundenliste', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });
    await page.route(`${API_URL}/customers/`, async (route) => {
      await route.fulfill({ json: [] });
    });

    await page.goto(CUSTOMER_URL);
    await page.getByRole('button', { name: /Zurück zur Übersicht/ }).click();

    await expect(page).toHaveURL('/');
  });

  test('zeigt "Keine Adresse hinterlegt" wenn keine Rechnungsadresse vorhanden', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });

    await page.goto(CUSTOMER_URL);

    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await expect(billingSection.getByText('Keine Adresse hinterlegt')).toBeVisible();
  });

  test('zeigt "Keine Adresse hinterlegt" wenn keine Lieferadresse vorhanden', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });

    await page.goto(CUSTOMER_URL);

    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await expect(deliverySection.getByText('Keine Adresse hinterlegt')).toBeVisible();
  });

  test('zeigt "Hinzufügen"-Button wenn keine Adresse vorhanden', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });

    await page.goto(CUSTOMER_URL);

    await expect(page.getByRole('button', { name: 'Hinzufügen' })).toHaveCount(2);
  });

  test('zeigt "Bearbeiten"-Button wenn Adresse vorhanden', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithAddresses });
    });

    await page.goto(CUSTOMER_URL);

    await expect(page.getByRole('button', { name: 'Bearbeiten' })).toHaveCount(2);
  });

  test('zeigt Adressdaten korrekt an', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithAddresses });
    });

    await page.goto(CUSTOMER_URL);

    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await expect(billingSection.getByText('Max Mustermann')).toBeVisible();
    await expect(billingSection.getByText(/Musterstraße 1/)).toBeVisible();
    await expect(billingSection.getByText(/12345 Musterstadt/)).toBeVisible();

    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await expect(deliverySection.getByText('Erika Musterfrau')).toBeVisible();
    await expect(deliverySection.getByText(/Beispielweg 2/)).toBeVisible();
    await expect(deliverySection.getByText(/54321 Beispielstadt/)).toBeVisible();
  });
});

test.describe('Rechnungsadresse bearbeiten', () => {
  test.beforeEach(async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });
    await page.goto(CUSTOMER_URL);

    const billingSection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Rechnungsadresse' }),
    });
    await billingSection.getByRole('button', { name: 'Hinzufügen' }).click();
  });

  test('Hinzufügen-Button öffnet das Formular', async ({ page }) => {
    await expect(page.locator('#recipient')).toBeVisible();
  });

  test('Abbrechen schließt das Formular', async ({ page }) => {
    await page.getByRole('button', { name: 'Abbrechen' }).click();

    await expect(page.locator('#recipient')).not.toBeVisible();
  });

  test('zeigt Fehler bei leerem Empfänger', async ({ page }) => {
    await page.locator('#recipient').fill('Max');
    await page.locator('#recipient').fill('');

    await expect(page.locator('.field-error')).toContainText('Empfänger ist erforderlich');
  });

  test('speichert Rechnungsadresse erfolgreich', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}/billing-address`, async (route) => {
      await route.fulfill({ status: 204 });
    });

    await page.locator('#recipient').fill('Max Mustermann');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await expect(page.locator('#recipient')).not.toBeVisible();
  });
});

test.describe('Lieferadresse bearbeiten', () => {
  test.beforeEach(async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}`, async (route) => {
      await route.fulfill({ json: customerWithoutAddresses });
    });
    await page.goto(CUSTOMER_URL);

    const deliverySection = page.locator('.address-section', {
      has: page.locator('h3', { hasText: 'Lieferadresse' }),
    });
    await deliverySection.getByRole('button', { name: 'Hinzufügen' }).click();
  });

  test('zeigt Fehler bei leerer PLZ', async ({ page }) => {
    await page.locator('#zipCode').fill('12345');
    await page.locator('#zipCode').fill('');

    await expect(page.locator('.field-error')).toContainText('PLZ muss aus exakt 5 Zahlen bestehen');
  });

  test('zeigt Fehler bei ungültiger PLZ', async ({ page }) => {
    await page.locator('#zipCode').fill('abc12');

    await expect(page.locator('.field-error')).toContainText('PLZ muss aus exakt 5 Zahlen bestehen');
  });

  test('zeigt Fehler wenn PLZ und Ort nicht zusammenpassen', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}/delivery-address`, async (route) => {
      await route.fulfill({
        status: 400,
        contentType: 'application/problem+json',
        body: JSON.stringify({
          type: 'about:blank',
          title: 'Bad Request',
          status: 400,
          detail: 'Die Postleitzahl 12345 gehört nicht zum Ort Oldenburg.',
        }),
      });
    });

    await page.locator('#recipient').fill('Max Mustermann');
    await page.locator('#zipCode').fill('12345');
    await page.locator('#cityName').fill('Oldenburg');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await expect(page.locator('.error-message')).toContainText(
      'Die Postleitzahl 12345 gehört nicht zum Ort Oldenburg.'
    );
  });

  test('speichert Lieferadresse erfolgreich', async ({ page }) => {
    await page.route(`${API_URL}/customers/${CUSTOMER_NUMBER}/delivery-address`, async (route) => {
      await route.fulfill({ status: 204 });
    });

    await page.locator('#recipient').fill('Max Mustermann');
    await page.locator('#zipCode').fill('26122');
    await page.locator('#cityName').fill('Oldenburg');
    await page.getByRole('button', { name: 'Speichern' }).click();

    await expect(page.locator('#recipient')).not.toBeVisible();
  });
});
