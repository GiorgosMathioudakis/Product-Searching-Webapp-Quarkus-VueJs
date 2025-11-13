// oxlint-disable no-unused-vars
import { defineStore } from "pinia";

import axios from "axios";

import { ref } from "vue";
import { VListItemSubtitle } from "vuetify/components";

export const useProductStore = defineStore("product", () => {
  const product = ref([]);
  const loading = ref(true);
  const hasNext = ref(false);
  const items = ref();
  const hasPrev = ref(true);

  const searchName = ref("");
  const searchSku = ref("");
  const pageNo = ref(1);
  const pageSize = ref(10);
  const sortBy = ref("created_on");
  const sortDir = ref("DESC");

  async function fetchProducts() {
    loading.value = true;
    try {

      const params = {
        name: searchName.value,
        sku: searchSku.value,
        pageNo: pageNo.value,
        pageSize: pageSize.value,
        sortBy: sortBy.value,
        sortDir: sortDir.value,
      };

      loading.value = true;

      const response = await axios.get(`http://localhost:8080/products`, {params});

      items.value = response.data.products;
      hasNext.value = response.data.hasNext;
      hasPrev.value = ( params.pageNo > 1);

    } catch (error) {
      console.error("Failed to fetch products:", error);
    } finally {
      loading.value = false;
    }


  }

  async function createNewProduct(productData,params) {
    try {

      await axios.post("/products", productData);

      await fetchProducts(params);

    } catch (error) {
      console.error("Error during create request:", error);
    }
  }

  async function updateProduct(productData,params) {
    try {

      await axios.put(`http://localhost:8080/products/${productData.id}`,productData);

      await fetchProducts(params);

    } catch (error) {
      console.error("Error during update request: ", error);
    }
  }

  async function removeProduct(productId,params) {
    try {

      await axios.delete(`http://localhost:8080/products/${productId}`);

      await fetchProducts(params);

    } catch (error) {
      console.error("Error during remove request: ", error);
    }
  }

  return {
    product,
    items,
    hasNext,
    hasPrev,
    loading,
    searchName,
    searchSku,
    pageNo,
    pageSize,
    sortBy,
    sortDir,
    fetchProducts,
    createNewProduct,
    updateProduct,
    removeProduct,
  };
});
