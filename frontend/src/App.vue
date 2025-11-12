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

let debounceTimer;

watch(
  [searchName, searchSku, pageSize , sortBy, sortDir],
  () => {

    if(pageNo.value !== 1){

      pageNo.value = 1;

    }else{

      clearTimeout(debounceTimer);

      debounceTimer = setTimeout(() => {
          productStore.fetchProducts({
            name: searchName.value,
            sku: searchSku.value,
            pageNo: pageNo.value,
            pageSize: pageSize.value,
            sortBy: sortBy.value,
            sortDir: sortDir.value
          });
      }, 400);

    }
  },
  {
    deep: true,
  }
);


watch(
  [pageNo],
  () => {
    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        productStore.fetchProducts({
          name: searchName.value,
          sku: searchSku.value,
          pageNo: pageNo.value,
          pageSize: pageSize.value,
          sortBy: sortBy.value,
          sortDir: sortDir.value
        });
    }, 400);
  },
  {
    immediate: true,
  }
)

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
  productDialog.value.edit = false;

  productDialog.value = {
    id: null,
    name: "",
    sku: "",
    description: "",
    price: 0,
  };

  productDialog.value.show = true;
}

function openEditDialog(product) {

  productDialog.value.edit = true;

  productDialog.value = product;

  productDialog.value.show = true;

}

function closeDialog() {
  productDialog.value.show = false;
}

function handleSave(){
  if (productDialog.value.edit) {
    productStore.updateProduct(productDialog.value);
  } else {
    productStore.createNewProduct(productDialog.value);
  }
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

</template>



<style scoped>
:deep(th) {
  font-weight: bold !important;
  color: #333 !important;
  background-color: #f9f9f9;
}
</style>
