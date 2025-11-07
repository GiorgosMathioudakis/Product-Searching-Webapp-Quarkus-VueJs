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

  async function fetchProducts(params) {
    loading.value = true;
    try {

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

  async function createNewProduct(productData) {
    try {
      await axios.post("/products", productData);

      await fetchProducts();
    } catch (error) {
      console.error("Error during create request:", error);
    }
  }

  async function updateProduct(productData) {
    try {
      console.log("product " + productData + " with id " + productData.id);
      await axios.put(`http://localhost:8080/products/${productData.id}`,productData);

      await fetchProducts();
    } catch (error) {
      console.error("Error during update request: ", error);
    }
  }

  async function removeProduct(productId) {
    try {
      await axios.delete("/products/" + productId);

      await fetchProducts();
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
    fetchProducts,
    createNewProduct,
    updateProduct,
    removeProduct,
  };
});
