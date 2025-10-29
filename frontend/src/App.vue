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

    <template v-slot:item.createdOn="{ item }">
      <span>{{ formatDateTime(item.createdOn) }}</span>
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
import { ref, onMounted } from "vue";

// 1. STATE
// Holds the product data
const products = ref([]);
// Controls the loading spinner
const loading = ref(true);

// Defines the columns for the v-data-table.
// 'title' is the displayed text.
// 'key' is the property name in your product object.
const headers = ref([
  { title: "Product Name", key: "name", minWidth: "100px", sortable: false },
  { title: "Price", key: "price" },
  { title: "SKU", key: "sku", sortable: false },
  {
    title: "Description",
    key: "description",
    minWidth: "150px",
    sortable: false,
  },
  { title: "Created On", key: "createdOn" },
  {
    title: "Actions",
    key: "actions",
    sortable: false,
    align: "end",
    width: "120px",
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
    const response = await fetch("/products");

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    products.value = await response.json();
  } catch (error) {
    console.error("Failed to fetch products:", error);
  } finally {
    loading.value = false;
  }
}

function formatDateTime(isoString) {
  if (!isoString) return 'N/A';

  const date = new Date(isoString);

  // Define the desired format
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  };

  // By passing 'undefined' as the first argument, we tell
  // toLocaleString() to use the browser's default locale (language).
  // It will *also* automatically use the browser's default timezone.
  return date.toLocaleString(undefined, options);
}

function editProduct(product) {
  console.log("EDIT product:", product.id, product.name);
}

function deleteProduct(product) {
  console.log("DELETE product:", product.id, product.name);
}
</script>

<style scoped>
:deep(th) {
  font-weight: bold !important;
  color: #333 !important;
  background-color: #f9f9f9;
}
</style>
