// @ts-check
import { test, expect } from '@playwright/test';

const PRODUCT_NAME = 'Test Product';
const PRODUCT_SKU = 'TEST-SKU-001';
const PRODUCT_DESCRIPTION = 'This is a test product for e2e testing.';
const PRODUCT_PRICE = '99.99';

const UPDATED_PRODUCT_NAME = 'Updated Test Product';
const UPDATED_PRODUCT_SKU = 'TEST-SKU-002';
const UPDATED_PRODUCT_PRICE = '109.99';


test.beforeEach(async ({ page }) => {

  await page.goto('http://localhost:3000');

  await page.getByRole('button', { name: 'New Product' }).click();
  await page.getByRole('textbox', { name: 'Name Name' }).fill(PRODUCT_NAME);
  await page.getByRole('textbox', { name: 'SKU SKU' }).fill(PRODUCT_SKU);
  await page.getByRole('textbox', { name: 'Description Description' }).fill(PRODUCT_DESCRIPTION);
  await page.getByRole('spinbutton', { name: 'Price Price' }).fill(PRODUCT_PRICE);
  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

});

test.afterEach(async ({ page }) => {
  await page.goto('http://localhost:3000');

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  const originalProductRow = page.getByRole('row', { name: new RegExp(`^${PRODUCT_NAME}`) });

  if (await originalProductRow.count() > 0) {
    await originalProductRow.first().getByLabel('Delete product').click();
    await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });
  }

  const updatedProductRow = page.getByRole('row', { name: new RegExp(`^${UPDATED_PRODUCT_NAME}`) });

  if (await updatedProductRow.count() > 0) {
    await updatedProductRow.first().getByLabel('Delete product').click();
    await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });
  }

});

test('search product by name', async ({ page }) => {

  await page.getByRole('textbox', { name: 'Search by Name Search by Name' }).fill(PRODUCT_NAME);
  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: PRODUCT_NAME, exact: true }).first()).toBeVisible();

  await page.getByRole('textbox', { name: 'Search by Name Search by Name' }).fill('NonExistentProduct');
  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });
  await expect(page.getByRole('cell', { name: PRODUCT_NAME, exact: true })).toBeHidden();

});

test('search product by sku', async ({ page }) => {

  await page.getByRole('textbox', { name: 'Search by SKU Search by SKU' }).fill(PRODUCT_SKU);
  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: PRODUCT_SKU, exact: true }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: PRODUCT_NAME, exact: true }).first()).toBeVisible();

});

test('create product', async ({ page }) => {

  await expect(page.getByRole('cell', { name: PRODUCT_NAME, exact: true }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: PRODUCT_SKU, exact: true }).first()).toBeVisible();

});

test('update product', async ({ page }) => {

  await page.getByRole('row', { name: new RegExp(`^${PRODUCT_NAME}`) }).getByLabel('Edit product').click();

  await page.getByRole('textbox', { name: 'Name Name' }).fill(UPDATED_PRODUCT_NAME);
  await page.getByRole('textbox', { name: 'SKU SKU' }).fill(UPDATED_PRODUCT_SKU);
  await page.getByRole('spinbutton', { name: 'Price Price' }).fill(UPDATED_PRODUCT_PRICE);

  await page.getByRole('button', { name: 'Save' }).click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: PRODUCT_NAME, exact: true })).toBeHidden();
  await expect(page.getByRole('cell', { name: UPDATED_PRODUCT_NAME, exact: true }).first()).toBeVisible();
  await expect(page.getByRole('cell', { name: UPDATED_PRODUCT_SKU, exact: true }).first()).toBeVisible();

});

test('delete product', async ({ page }) => {

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await page.getByRole('row', { name: new RegExp(`^${PRODUCT_NAME}`) }).getByLabel('Delete product').click();

  await expect(page.getByText('Loading products...')).toBeHidden({ timeout: 10000 });

  await expect(page.getByRole('cell', { name: PRODUCT_NAME, exact: true }).first()).toBeHidden();

});
