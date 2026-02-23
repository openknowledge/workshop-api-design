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
import { PactV4 } from '@pact-foundation/pact';
import { createProvider, setupApiProxy } from './pact-proxy';

const customers = [
  { number: '007', name: 'James Bond' },
  { number: '0815', name: 'Max Mustermann' },
  { number: '0816', name: 'Erika Mustermann' },
];

function addCustomersListInteraction(provider: PactV4) {
  return provider
    .addInteraction()
    .uponReceiving('a request to get all customers')
    .withRequest('GET', '/customers/')
    .willRespondWith(200, (builder) => {
      builder.headers({ 'Content-Type': 'application/json' }).jsonBody(customers);
    });
}

test('zeigt Kunden mit Kundennummer und Name in der Tabelle', async ({ page }) => {
  await addCustomersListInteraction(createProvider()).executeTest(async (mockServer) => {
    await setupApiProxy(page, mockServer.url);
    await page.goto('/');

    await expect(page.getByRole('cell', { name: '0815' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Max Mustermann' })).toBeVisible();
    await expect(page.getByRole('cell', { name: '0816' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'Erika Mustermann' })).toBeVisible();
    await expect(page.getByRole('cell', { name: '007' })).toBeVisible();
    await expect(page.getByRole('cell', { name: 'James Bond' })).toBeVisible();
  });
});

test('navigiert zu Kundendetails beim Klick auf eine Zeile', async ({ page }) => {
  const provider = createProvider();
  addCustomersListInteraction(provider);
  await provider
    .addInteraction()
    .uponReceiving('a request to get customer 0815')
    .withRequest('GET', '/customers/0815')
    .willRespondWith(200, (builder) => {
      builder
        .headers({ 'Content-Type': 'application/json' })
        .jsonBody({ number: '0815', name: 'Max Mustermann' });
    })
    .executeTest(async (mockServer) => {
      await setupApiProxy(page, mockServer.url);
      await page.goto('/');
      await page.getByRole('cell', { name: 'Max Mustermann' }).click();

      await expect(page).toHaveURL('/customers/0815');
      await page.waitForLoadState('networkidle');
    });
});

test('navigiert zu Neuer Kunde beim Klick auf den Button', async ({ page }) => {
  await addCustomersListInteraction(createProvider()).executeTest(async (mockServer) => {
    await setupApiProxy(page, mockServer.url);
    await page.goto('/');
    await page.getByRole('button', { name: 'Neuer Kunde' }).click();

    await expect(page).toHaveURL('/customers/new');
  });
});

test('sortiert nach Kundennummer aufsteigend und absteigend', async ({ page }) => {
  await addCustomersListInteraction(createProvider()).executeTest(async (mockServer) => {
    await setupApiProxy(page, mockServer.url);
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
});

test('sortiert nach Name aufsteigend und absteigend', async ({ page }) => {
  await addCustomersListInteraction(createProvider()).executeTest(async (mockServer) => {
    await setupApiProxy(page, mockServer.url);
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
});
