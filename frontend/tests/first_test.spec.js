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


test( 'create product ', async({ page }) => {

  await page.goto('http://localhost:3000');

  await page.getByRole('button', { name: 'New Product' }).click();

  await page.getByRole('textbox', { name: 'Name Name' }).fill('T-Shirt');

  await page.getByRole('textbox', { name: 'SKU SKU' }).fill('GR-1');

  await page.getByRole('textbox', { name: 'Description Description' }).fill('Carhartt T-Shirt');

  await page.getByRole('spinbutton', { name: 'Price Price' }).fill('20');

  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'T-Shirt', exact: true }).first()).toBeVisible();

});

test( 'update product ', async({ page }) => {

  await page.goto('http://localhost:3000');

  await page.getByRole('row', { name: 'T-Shirt $20.00 GR-1 Carhartt' }).getByLabel('Edit product').click();

  await page.getByRole('textbox', { name: 'Name Name' }).fill('Old T-Shirt');

  await page.getByRole('textbox', { name: 'SKU SKU' }).fill('GR-2');

  await page.getByRole('textbox', { name: 'Description Description' }).fill('Old Carhartt T-Shirt');

  await page.getByRole('spinbutton', { name: 'Price Price' }).fill('15');

  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'Old T-Shirt', exact: true }).first()).toBeVisible();

});
