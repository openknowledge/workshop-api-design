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

const customers = [
  { number: '007', name: 'James Bond' },
  { number: '0815', name: 'Max Mustermann' },
  { number: '0816', name: 'Erika Mustermann' },
];

test.beforeEach(async ({ page }) => {
  await page.route(`${API_URL}/customers/`, async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify(customers),
    });
  });
});

test('zeigt Kunden mit Kundennummer und Name in der Tabelle', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('cell', { name: '0815' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Max Mustermann' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '0816' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Erika Mustermann' })).toBeVisible();
  await expect(page.getByRole('cell', { name: '007' })).toBeVisible();
  await expect(page.getByRole('cell', { name: 'James Bond' })).toBeVisible();
});

test('navigiert zu Kundendetails beim Klick auf eine Zeile', async ({ page }) => {
  await page.route(`${API_URL}/customers/0815`, async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ number: '0815', name: 'Max Mustermann' }),
    });
  });

  await page.goto('/');
  await page.getByRole('cell', { name: 'Max Mustermann' }).click();

  await expect(page).toHaveURL('/customers/0815');
});

test('navigiert zu Neuer Kunde beim Klick auf den Button', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('button', { name: 'Neuer Kunde' }).click();

  await expect(page).toHaveURL('/customers/new');
});

test('sortiert nach Kundennummer aufsteigend und absteigend', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('columnheader', { name: 'Kundennummer' }).click();
  let numbers = await page.locator('tbody tr td:first-child').allTextContents();
  expect(numbers.indexOf('007')).toBeLessThan(numbers.indexOf('0815'));
  expect(numbers.indexOf('0815')).toBeLessThan(numbers.indexOf('0816'));

  await page.getByRole('columnheader', { name: 'Kundennummer' }).click();
  numbers = await page.locator('tbody tr td:first-child').allTextContents();
  expect(numbers.indexOf('0816')).toBeLessThan(numbers.indexOf('0815'));
  expect(numbers.indexOf('0815')).toBeLessThan(numbers.indexOf('007'));
});

test('sortiert nach Name aufsteigend und absteigend', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('columnheader', { name: 'Name' }).click();
  let names = await page.locator('tbody tr td:nth-child(2)').allTextContents();
  expect(names.indexOf('Erika Mustermann')).toBeLessThan(names.indexOf('James Bond'));
  expect(names.indexOf('James Bond')).toBeLessThan(names.indexOf('Max Mustermann'));

  await page.getByRole('columnheader', { name: 'Name' }).click();
  names = await page.locator('tbody tr td:nth-child(2)').allTextContents();
  expect(names.indexOf('Max Mustermann')).toBeLessThan(names.indexOf('James Bond'));
  expect(names.indexOf('James Bond')).toBeLessThan(names.indexOf('Erika Mustermann'));
});
