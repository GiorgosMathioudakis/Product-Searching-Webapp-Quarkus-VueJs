// oxlint-disable no-unused-vars
import { defineStore } from "pinia";

import axios from "axios";

import { ref } from "vue";

export const useProductStore = defineStore("product", () => {
  const product = ref([]);
  const loading = ref(true);
  const hasNext = ref(false);

  async function fetchProducts(name, sku, page, itemsPerPage, sortBy, sortDir) {
    loading.value = true;
    try {
      
      const params = new URLSearchParams();

      params.append('name', name); 
      params.append('sku', sku);
      params.append('page', page);
      params.append('pageSize', itemsPerPage);
      params.append('sortBy', sortBy);
      params.append('sortDir', sortDir);


      const response = await axios.get("/products", {params});
      product.value = response.data.items;
      hasMore.value = response.data.hasMore;

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
    loading,
    fetchProducts,
    createNewProduct,
    updateProduct,
    removeProduct,
  };
});
