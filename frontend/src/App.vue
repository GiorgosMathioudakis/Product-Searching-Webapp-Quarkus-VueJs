<script setup>
import { onMounted, ref, watch } from "vue";
import { useProductStore } from "@/stores/product.js";
import Dialog from "@/components/Dialog.vue";

const productStore = useProductStore();

const headers = ref([
  { title: "Name", key: "name", minWidth: "100px", sortable: false },
  { title: "Price", key: "price", sortable: false },
  { title: "SKU", key: "sku", sortable: false },
  { title: "Description", key: "description", minWidth: "150px", sortable: false },
  { title: "Created On", key: "createdOn", sortable: false },
  { title: "Updated On", key: "updatedOn", sortable: false },
  { title: "Actions", key: "actions", sortable: false, align: "end", width: "120px" },
]);

const dialogVisible = ref(false);
const dialogTitle = ref("");
const isEditMode = ref(false);
const searchName = ref("");
const searchSku = ref("");
const sortBy = ref("createdOn");
const sortDir = ref("desc");
const pageNo = ref(1);
const pageSize = ref(10);


const  {
  items,
  loading,
  error,
  currentPage,
  hasPrev,
  hasNext,
  refetch,
  } = productStore.fetchProducts({
    searchName,
    searchSku,
    pageNo,
    pageSize,
    sortBy,
    sortDir
  });


const tableOptions = ref({
  currentPage: 1,
  itemsPerPage: 10
});

const sortableColumns = ref([
  { title: "Date Created", value: "created_on" },
  { title: "Date Updated", value: "updated_on" },
  { title: "Price", value: "price" },
]);
const sortDirections = ref([
  { title: "Descending", value: "DESC" },
  { title: "Ascending", value: "ASC" },
]);


const product = ref({
  id: null,
  name: "",
  sku: "",
  description: "",
  price: 0,
});

let debounceTimer;

watch(
  [searchName, searchSku, tableOptions, sortBy, sortDir],
  () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      productStore.fetchProducts(
        searchName.value,
        searchSku.value,
        tableOptions.value.page,
        tableOptions.value.itemsPerPage,
        sortBy.value,
        sortDir.value
      );
    }, 400);
  },
  {
    deep: true,
    immediate: true,
  }
);

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

  product.value = {
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
  dialogTitle.value = "Edit product";

  product.value = { ...product };

  dialogVisible.value = true;
}

function closeDialog() {
  dialogVisible.value = false;
}

</script>

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
    <v-card-text>
      <v-row>
        <v-col cols="12" md="2">
          <v-text-field
            v-model="searchName"
            label="Search by Name"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-text-field
            v-model="searchSku"
            label="Search by SKU"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            hide-details
            clearable
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-select
          v-model="sortBy"
          :items="sortableColumns"
          label="Sort By"
          variant="outlined"
          density="compact"
          hide-details
          ></v-select>
        </v-col>
        <v-col cols="12" md="2">
          <v-select
            v-model="sortDir"
            :items="sortDirections"
            label="Direction"
            variant="outlined"
            density="compact"
            hide-details
          ></v-select>
        </v-col>
      </v-row>
    </v-card-text>
    <v-data-table-server
      class="elevation-1"
      :headers="headers"
      item-value="id"
      :items="productStore.product"
      :loading="productStore.loading"
      loading-text="Loading products..."
      no-data-text="No products found."
      v-model:items-per-page="tableOptions.itemsPerPage"
      @update:options="tableOptions = $event"
      hide-default-footer
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
      <template v-slot:bottom>
        <div class="text-center dark pt-2">
          <div>
            Items Per Page:
          </div>
          <select>
            <option
              v-for="option in [5, 10, 15, 20]"
              :key="option"
              :value="option"
              @click="tableOptions.itemsPerPage = option"
              ></option>
          </select>
          <v-pagination
            v-model="page"
          ></v-pagination>
        </div>
      </template>
    </v-data-table-server>
  </v-card>

  <Dialog
    v-model="dialogVisible"
    :isEditMode="isEditMode"
    :dialogTitle="dialogTitle"
    :product="product"
    :isVisible="dialogVisible"
    @close="closeDialog"
  ></Dialog>
</template>



<style scoped>
:deep(th) {
  font-weight: bold !important;
  color: #333 !important;
  background-color: #f9f9f9;
}
</style>
