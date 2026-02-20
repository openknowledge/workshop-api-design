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

const mockCustomers = [
  { number: '0816', name: 'Müller' },
  { number: '0815', name: 'Schmidt' },
];

test.beforeEach(async ({ page }) => {
  await page.route(`${API_URL}/customers/`, async (route) => {
    await route.fulfill({ json: mockCustomers });
  });
});

test('zeigt Kunden mit Kundennummer und Name in der Tabelle', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('cell', { name: '0816' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Müller' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '0815' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Schmidt' })).toBeVisible();
});

test('navigiert zu Kundendetails beim Klick auf eine Zeile', async ({ page }) => {
  await page.route(`${API_URL}/customers/0816`, async (route) => {
    await route.fulfill({ json: mockCustomers[0] });
  });

  await page.goto('/');
  await page.getByRole('cell', { name: 'Müller' }).click();

  await expect(page).toHaveURL('/customers/0816');
});

test('navigiert zu Neuer Kunde beim Klick auf den Button', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('button', { name: 'Neuer Kunde' }).click();

  await expect(page).toHaveURL('/customers/new');
});

test('sortiert nach Kundennummer aufsteigend und absteigend', async ({ page }) => {
  await page.goto('/');

  const rows = page.locator('tbody tr');

  // Unsortiert: Reihenfolge aus API (0816, 0815)
  await expect(rows.nth(0).getByRole('cell').first()).toHaveText('0816');
  await expect(rows.nth(1).getByRole('cell').first()).toHaveText('0815');

  // Klick → aufsteigend (0815, 0816)
  await page.getByRole('columnheader', { name: 'Kundennummer' }).click();
  await expect(rows.nth(0).getByRole('cell').first()).toHaveText('0815');
  await expect(rows.nth(1).getByRole('cell').first()).toHaveText('0816');

  // Klick → absteigend (0816, 0815)
  await page.getByRole('columnheader', { name: 'Kundennummer' }).click();
  await expect(rows.nth(0).getByRole('cell').first()).toHaveText('0816');
  await expect(rows.nth(1).getByRole('cell').first()).toHaveText('0815');
});

test('sortiert nach Name aufsteigend und absteigend', async ({ page }) => {
  await page.goto('/');

  const rows = page.locator('tbody tr');

  // Klick → aufsteigend (Müller, Schmidt)
  await page.getByRole('columnheader', { name: 'Name' }).click();
  await expect(rows.nth(0).getByRole('cell').nth(1)).toHaveText('Müller');
  await expect(rows.nth(1).getByRole('cell').nth(1)).toHaveText('Schmidt');

  // Klick → absteigend (Schmidt, Müller)
  await page.getByRole('columnheader', { name: 'Name' }).click();
  await expect(rows.nth(0).getByRole('cell').nth(1)).toHaveText('Schmidt');
  await expect(rows.nth(1).getByRole('cell').nth(1)).toHaveText('Müller');
});
