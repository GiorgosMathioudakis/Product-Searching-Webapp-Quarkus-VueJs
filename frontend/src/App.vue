<template>
  <v-data-table
    :headers="headers"
    :items="products"
    :loading="loading"
    item-value="id"
    class="elevation-1"
    loading-text="Loading products..."
    no-data-text="No products found."
  >
    <template v-slot:item.price="{ item }">
      <span>${{ item.price.toFixed(2) }}</span>
    </template>


    <template v-slot:item.actions="{ item }">
      <v-btn
        icon="mdi-pencil"
        variant="text"
        color="primary"
        size="small"
        class="me-2"
        @click="editProduct(item)"
        aria-label="Edit product"
      ></v-btn>
      <v-btn
        icon="mdi-delete"
        variant="text"
        color="error"
        size="small"
        @click="deleteProduct(item)"
        aria-label="Delete product"
      ></v-btn>
    </template>
  </v-data-table>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 1. STATE
// Holds the product data
const products = ref([]);
// Controls the loading spinner
const loading = ref(true);

// Defines the columns for the v-data-table.
// 'title' is the displayed text.
// 'key' is the property name in your product object.
const headers = ref([
  { title: 'Product Name', key: 'name', minWidth: '200px' },
  { title: 'SKU', key: 'sku' },
  { title: 'Description', key: 'description', minWidth: '300px' },
  { title: 'Price', key: 'price', align: 'end' },
  {
    title: 'Actions',
    key: 'actions',
    sortable: false,
    align: 'end',
    width: '120px'
  },
]);

// 2. LIFECYCLE
// onMounted is a Vue hook that runs once when the component is
// first added to the page. Perfect for fetching initial data.
onMounted(async () => {
  await fetchProducts();
});

// 3. METHODS
async function fetchProducts() {
  loading.value = true;
  try {
    // We use a simple fetch API.
    // Assumes your Quarkus app is on the same host/port.
    // If not, replace '/products' with the full URL
    // (e.g., 'http://localhost:8080/products')
    const response = await fetch('/products');

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    products.value = await response.json();

  } catch (error) {
    console.error("Failed to fetch products:", error);
    // In a real app, you'd show a v-alert or snackbar here
  } finally {
    loading.value = false;
  }
}

function editProduct(product) {
  console.log('EDIT product:', product.id, product.name);
  // Your logic to open an edit dialog/modal would go here
  // You can pass the 'product' object to it.
}

function deleteProduct(product) {
  console.log('DELETE product:', product.id, product.name);
  // Your logic for a confirmation dialog would go here.
  // After confirmation, you would call:
  // await fetch(`/products/${product.id}`, { method: 'DELETE' });
  // And then refresh the table:
  // await fetchProducts();
}
</script>

<style scoped>
/* Scoped CSS applies only to this component */
/* The v-data-table is already well-styled, but you can override here */
:deep(th) {
  font-weight: bold !important;
  color: #333 !important;
  background-color: #f9f9f9;
}
</style>
