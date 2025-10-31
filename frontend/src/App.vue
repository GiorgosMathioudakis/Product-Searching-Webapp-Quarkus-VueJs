<template>
  <v-card class="elevation-2">
    <v-toolbar color="surface" density="compact">
      <v-toolbar-title>Products</v-toolbar-title>
      <v-spacer />
      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        variant="flat"
        @click="openCreateDialog"
      >
        New Product
      </v-btn>
    </v-toolbar>
    <v-data-table
      class="elevation-1"
      :headers="headers"
      item-value="id"
      :items="productStore.products"
      :loading="productStore.loading"
      loading-text="Loading products..."
      no-data-text="No products found."
    >
      <template #item.price="{ item }">
        <span>${{ item.price.toFixed(2) }}</span>
      </template>

      <template #item.createdOn="{ item }">
        <span>{{ formatDateTime(item.createdOn) }}</span>
      </template>

      <template #item.updatedOn="{ item }">
        <span>{{ formatDateTime(item.updatedOn) }}</span>
      </template>

      <template #item.actions="{ item }">
        <v-btn
          aria-label="Edit product"
          class="me-2"
          color="primary"
          icon="mdi-pencil"
          size="small"
          variant="text"
          @click="openEditDialog(item)"
        />
        <v-btn
          aria-label="Delete product"
          color="error"
          icon="mdi-delete"
          size="small"
          variant="text"
          @click="productStore.removeProduct(item.id)"
        />
      </template>
    </v-data-table>
  </v-card>

  <v-dialog v-model="dialogVisible" max-width="600px" persistent>
    <v-card>
      <!-- The title is dynamic -->
      <v-card-title class="pa-4 bg-primary">
        <span class="text-h5">{{ dialogTitle }}</span>
      </v-card-title>

      <v-card-text>
        <v-form>
          <v-container>
            <!-- Form fields, bound to our productForm ref -->
            <v-text-field
              v-model="productForm.name"
              density="compact"
              label="Name"
              variant="outlined"
            />
            <v-text-field
              v-model="productForm.sku"
              density="compact"
              label="SKU"
              variant="outlined"
            />
            <v-text-field
              v-model="productForm.description"
              density="compact"
              label="Description"
              variant="outlined"
            />
            <v-text-field
              v-model.number="productForm.price"
              density="compact"
              label="Price"
              prefix="$"
              type="number"
              variant="outlined"
            />
          </v-container>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue-grey-darken-1" variant="text" @click="closeDialog">
          Cancel
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="flat"
          @click="productStore.saveProduct"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useProductStore } from "@/stores/product.js";

const productStore = useProductStore();

const headers = ref([
  { title: "Name", key: "name", minWidth: "100px", sortable: false },
  { title: "Price", key: "price" },
  { title: "SKU", key: "sku", sortable: false },
  {
    title: "Description",
    key: "description",
    minWidth: "150px",
    sortable: false,
  },
  { title: "Created On", key: "createdOn" },
  { title: "Updated On", key: "updatedOn" },
  {
    title: "Actions",
    key: "actions",
    sortable: false,
    align: "end",
    width: "120px",
  },
]);

const dialogVisible = ref(false); // Controls if the dialog is open
const dialogTitle = ref(""); // Holds the title "New Product" or "Edit Product"
const isEditMode = ref(false); // Tracks if we are editing or creating

const productForm = ref({
  id: null,
  name: "",
  sku: "",
  description: "",
  price: 0,
});

onMounted(async () => {
  await productStore.fetchProducts();
});

function formatDateTime(isoString) {
  if (!isoString) return "N/A";

  const date = new Date(isoString);

  const options = {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  };

  return date.toLocaleString(undefined, options);
}

function openCreateDialog() {
  isEditMode.value = false;
  dialogTitle.value = "New Product";

  productForm.value = {
    id: null,
    name: "",
    sku: "",
    description: "",
    price: 0,
  };

  dialogVisible.value = true;
}

function openEditDialog(product) {
  isEditMode.value = true;
  dialogTitle.value = "Edit Product";

  // Load a *copy* of the product's data into the form
  // We use {...product} to make a copy, so we don't
  // accidentally change the table data if the user cancels.
  productForm.value = { ...product };

  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}
</script>

<style scoped>
:deep(th) {
  font-weight: bold !important;
  color: #333 !important;
  background-color: #f9f9f9;
}
</style>
