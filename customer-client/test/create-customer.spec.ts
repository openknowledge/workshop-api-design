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

test.describe('Neuer Kunde', () => {
  test('zeigt Fehler bei leerem Namen', async ({ page }) => {
    // Given
    await page.goto('/customers/new');

    // When
    await page.getByLabel('Name *').fill('Max');
    await page.getByLabel('Name *').fill('');

    // Then
    await expect(page.locator('.field-error')).toContainText('Name ist erforderlich');
  });

  test('erstellt Kunden erfolgreich und navigiert zur Liste', async ({ page }) => {
    // Given
    await page.goto('/customers/new');

    // When
    await page.getByLabel('Name *').fill('Test Kunde');
    await page.getByRole('button', { name: 'Kunde erstellen' }).click();

    await expect(page.getByRole('cell', { name: 'Test Kunde' })).toBeVisible();
  });
});
