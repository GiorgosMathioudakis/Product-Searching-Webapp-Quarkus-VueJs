<script setup>
import { onMounted, ref, watch } from "vue";
import { useProductStore } from "@/stores/product.js";
import Dialog from "@/components/Dialog.vue";
import { useNotificationStore } from "@/stores/notification";
import { storeToRefs } from "pinia";

const productStore = useProductStore();
const notify = useNotificationStore();

const headers = ref([
  { title: "Name", key: "name", minWidth: "100px", sortable: false },
  { title: "Price", key: "price", sortable: false },
  { title: "SKU", key: "sku", sortable: false },
  { title: "Description", key: "description", width: "250px", sortable: false },
  { title: "Created On", key: "createdOn", sortable: false },
  { title: "Updated On", key: "updatedOn", sortable: false },
  { title: "Actions", key: "actions", sortable: false, align: "end", width: "120px" },
]);


const {
  searchName,
  searchSku,
  pageNo,
  pageSize,
  sortBy,
  sortDir
} = storeToRefs(productStore);

const sortableColumns = ref([
  { title: "Date Created", value: "created_on" },
  { title: "Date Updated", value: "updated_on" },
  { title: "Price", value: "price" },
]);
const sortDirections = ref([
  { title: "Descending", value: "DESC" },
  { title: "Ascending", value: "ASC" },
]);


const productDialog = ref({
  show:false,
  edit:false,
  id: null,
  name: "",
  sku: "",
  description: "",
  price: 0,
});

const getSafeValue = (text) => {
  return (text && text.length >= 3) ? text : "";
};

let debounceTimer;

watch(
  [searchName, searchSku, pageSize , sortBy, sortDir, pageNo],
  (newValues, oldValues) => {

    const [newName, newSku, newSize, newSort, newDir, newPage] = newValues;
    const [oldName, oldSku, oldSize, oldSort, oldDir, oldPage] = oldValues;

    const nameChanged = newName !== oldName;
    const skuChanged = newSku !== oldSku;
    const otherChanged = newSize !== oldSize || newSort !== oldSort || newDir !== oldDir || newPage !== oldPage;

    const nameInvalid = newName && newName.length > 0 && newName.length < 3;
    const skuInvalid = newSku && newSku.length > 0 && newSku.length < 3;

    if (nameChanged && nameInvalid) return;
    if (skuChanged && skuInvalid) return;

    if ((nameChanged || skuChanged || newSize !== oldSize || newSort !== oldSort || newDir !== oldDir) && newPage !== 1) {
      pageNo.value = 1;
      return;
    }

    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        productStore.fetchProducts({
          name: getSafeValue(newName),
          sku: getSafeValue(newSku),
          pageNo: newPage,
          pageSize: newSize,
          sortBy: newSort,
          sortDir: newDir
        });
    }, 400);

  },
  {
    deep: true,
  }
);

onMounted(() => {
  productStore.fetchProducts({
    name: getSafeValue(searchName.value),
    sku: getSafeValue(searchSku.value),
    pageNo: pageNo.value,
    pageSize: pageSize.value,
    sortBy: sortBy.value,
    sortDir: sortDir.value
  });
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

  productDialog.value = {
    id: null,
    name: "",
    sku: "",
    description: "",
    price: 0,
    show: true,
    edit: false
  };

}

function openEditDialog(product) {

  productDialog.value = {
    ...productDialog.value,
    ...product,
    edit: true,
    show: true
  };
}

function closeDialog() {
  productDialog.value.show = false;
}

function handleSave(productToSave){

  if (productToSave.edit) {

    productStore.updateProduct(productToSave);

  } else {

    productStore.createNewProduct(productToSave);

  }
}

</script>

<template>
  <v-card class="elevation-2">

    <v-toolbar color="surface" density="compact">
      <v-toolbar-title>Products</v-toolbar-title>
      <v-spacer />
      <v-btn
        id="new-product-button"
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
            clearable
            hide-details="auto"
            :hint="searchName && searchName.length > 0 && searchName.length < 3 ? 'Enter 3+ chars' : ''"
            persistent-hint
          ></v-text-field>
        </v-col>

        <v-col cols="12" md="2">
          <v-text-field
            v-model="searchSku"
            label="Search by SKU"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            clearable
            hide-details="auto"
            :hint="searchSku && searchSku.length > 0 && searchSku.length < 3 ? 'Enter 3+ chars' : ''"
            persistent-hint
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-select
            id="sort-by"
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
            id="sort-dir"
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
      id="data-table"
      class="elevation-1"
      :headers="headers"
      item-value="id"
      :items-length="pageSize"
      :items="productStore.items"
      :loading="productStore.loading"
      loading-text="Loading products..."
      no-data-text="No products found."
      v-model:items-per-page="pageSize"
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
        <v-card-text>
          <v-row align="center" justify="center">
            <v-col cols="auto">
              <v-select
                v-model="pageSize"
                :items="[10, 15, 20]"
                label="Items per page"
                density="compact"
                hide-details
                variant="outlined"
              ></v-select>
            </v-col>

            <v-col cols="auto">
              <v-pagination
                v-model="pageNo"
                :length="productStore.hasNext ? pageNo + 1 : pageNo"
                :total-visible="5"
                density="compact"
                class="float-right"
              ></v-pagination>
            </v-col>

          </v-row>
        </v-card-text>
      </template>
    </v-data-table-server>
  </v-card>

  <Dialog
    v-if="productDialog.show"
    v-model="productDialog.show"
    :dialogTitle=" productDialog.edit ? 'Edit Product' : 'New Product' "
    :product="productDialog"
    @close="closeDialog"
    @save="handleSave"
  ></Dialog>

  <v-snackbar
    v-model="notify.show"
    :color="notify.color"
    timeout="4000"
    location="top right"
  >
    {{ notify.message }}

    <template v-slot:actions>
      <v-btn variant="text" @click="notify.show = false">Close</v-btn>
    </template>
  </v-snackbar>

</template>



<style scoped>
:deep(th) {
  font-weight: bold !important;
  color: #333 !important;
  background-color: #f9f9f9;
}
</style>
