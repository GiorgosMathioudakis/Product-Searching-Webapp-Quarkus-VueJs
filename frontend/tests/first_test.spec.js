// @ts-check
import { test, expect } from '@playwright/test';

test.beforeEach(async ({ page }) => {

  await page.goto('http://localhost:3000');

});

test('search product by name', async ({ page }) => {

  await page.getByRole('textbox', { name: 'Search by Name Search by Name' }).fill('Cargo Shorts');

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'Cargo Shorts', exact: true }).first()).toBeVisible();

  await expect(page.getByRole('cell', { name: 'Polo Shirt', exact: true })).toBeHidden();
});

test( 'search product by sku', async ({ page }) => {

  await page.getByRole('textbox', { name: 'Search by SKU Search by SKU' }).fill('LJ-00004');

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'LJ-00004', exact: true }).first()).toBeVisible();

  await expect(page.getByRole('cell', { name: 'SH-00005', exact: true })).toBeHidden();

});


test( 'create product ', async({ page }) => {

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

  await page.getByRole('row', { name: 'T-Shirt $20.00 GR-1 Carhartt' }).getByLabel('Edit product').click();

  await page.getByRole('textbox', { name: 'Name Name' }).fill('Old T-Shirt');

  await page.getByRole('textbox', { name: 'SKU SKU' }).fill('GR-2');

  await page.getByRole('textbox', { name: 'Description Description' }).fill('Old Carhartt T-Shirt');

  await page.getByRole('spinbutton', { name: 'Price Price' }).fill('15');

  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'Old T-Shirt', exact: true }).first()).toBeVisible();

});

test( 'Delete product ', async({ page }) => {

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await page.getByRole('row', { name: 'Old T-Shirt $15.00 GR-2' }).getByLabel('Delete product').click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: 'Old T-Shirt', exact: true }).first()).toBeHidden();

});

// New tests for POST, PUT, DELETE lifecycle
test('create, update, and delete a product via UI', async ({ page }) => {
  
  // --- Create Product (POST equivalent) ---
  await page.getByRole('button', { name: 'New Product' }).click();
  await page.getByRole('textbox', { name: 'Name Name' }).fill('Test Product Alpha');
  await page.getByRole('textbox', { name: 'SKU SKU' }).fill('ALPHA-001');
  await page.getByRole('textbox', { name: 'Description Description' }).fill('Description for Test Product Alpha');
  await page.getByRole('spinbutton', { name: 'Price Price' }).fill('99');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });
  await expect(page.getByRole('cell', { name: 'Test Product Alpha', exact: true }).first()).toBeVisible();

  // --- Update Product (PUT equivalent) ---
  // Assuming the newly created product is the first row matching its name.
  await page.getByRole('row', { name: 'Test Product Alpha $99.00 ALPHA-001 Description for Test Product Alpha' }).getByLabel('Edit product').click();
  await page.getByRole('textbox', { name: 'Name Name' }).fill('Test Product Beta');
  await page.getByRole('textbox', { name: 'SKU SKU' }).fill('BETA-002');
  await page.getByRole('textbox', { name: 'Description Description' }).fill('Updated description for Test Product Beta');
  await page.getByRole('spinbutton', { name: 'Price Price' }).fill('105');
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });
  await expect(page.getByRole('cell', { name: 'Test Product Beta', exact: true }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: 'Test Product Alpha', exact: true })).toBeHidden(); // Ensure old product name is not visible

  // --- Delete Product (DELETE equivalent) ---
  await page.getByRole('row', { name: 'Test Product Beta $105.00 BETA-002 Updated description for Test Product Beta' }).getByLabel('Delete product').click();
  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });
  await expect(page.getByRole('cell', { name: 'Test Product Beta', exact: true }).first()).toBeHidden();

});
