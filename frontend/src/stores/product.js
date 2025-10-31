import { defineStore } from "pinia";

import axios from "axios";

import { ref } from "vue";

export const useProductStore = defineStore("product", () => {
  const products = ref([]);
  const loading = ref(true);
  const state = ref("");

  async function fetchProducts() {
    loading.value = true;
    try {
      const response = await axios.get("/products");
      products.value = response.data;
    } catch (error) {
      console.error("Failed to fetch products:", error);
    } finally {
      loading.value = false;
    }
  }

  async function saveProduct(product){
    
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
      await axios.put("/products/${productData.id}", productData);

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
    products,
    loading,
    fetchProducts,
    createNewProduct,
    updateProduct,
    removeProduct,
  };
});
