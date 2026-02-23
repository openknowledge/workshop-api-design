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
import type { Page } from '@playwright/test';
import { PactV4 } from '@pact-foundation/pact';
import path from 'path';

export const API_URL = process.env.VITE_API_URL || 'http://localhost:8181';

export function createProvider(): PactV4 {
  return new PactV4({
    consumer: 'customer-client',
    provider: 'customer-service',
    dir: path.resolve(process.cwd(), '../pacts'),
    logLevel: 'warn',
  });
}

export async function setupApiProxy(page: Page, mockServerUrl: string): Promise<void> {
  const baseUrl = mockServerUrl.replace(/\/$/, '');
  await page.route(`${API_URL}/**`, async (route) => {
    const proxiedUrl = route.request().url().replace(API_URL, baseUrl);
    const body = route.request().postDataBuffer();

    // Problematic headers that must not be forwarded to the mock server
    const { host, origin, referer, ...headers } = route.request().headers();
    void host;
    void origin;
    void referer;

    try {
      const nativeResponse = await fetch(proxiedUrl, {
        method: route.request().method(),
        headers,
        body: body || undefined,
      });
      const responseBody = await nativeResponse.arrayBuffer();
      await route.fulfill({
        status: nativeResponse.status,
        headers: Object.fromEntries(nativeResponse.headers.entries()),
        body: Buffer.from(responseBody),
      });
    } catch {
      // Request was aborted during navigation, ignore
    }
  });
}
