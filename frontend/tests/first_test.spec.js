// @ts-check
import { test, expect } from '@playwright/test';

test('search product by name', async ({ page }) => {

  await page.goto('http://localhost:3000/');

  await page.getByRole('textbox', { name: 'Search by Name Search by Name' }).fill('Cargo Shorts');

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'Cargo Shorts', exact: true }).first()).toBeVisible();

  await expect(page.getByRole('cell', { name: 'Polo Shirt', exact: true })).toBeHidden();
});

test( 'search product by sku', async ({ page }) => {

  await page.goto('http://localhost:3000/');

  await page.getByRole('textbox', { name: 'Search by SKU Search by SKU' }).fill('LJ-00004');

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'LJ-00004', exact: true }).first()).toBeVisible();

  await expect(page.getByRole('cell', { name: 'SH-00005', exact: true })).toBeHidden();

});
