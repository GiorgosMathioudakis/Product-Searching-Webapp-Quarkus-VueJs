import { defineStore } from "pinia";

import axios from "axios";

import { ref } from "vue";

export const useProductStore = defineStore("product", () => {
  const products = ref([]);
  const loading = ref(true);
  //create or edit
  const modalState = ref("create");

  async function fetchProducts(name, sku) {
    loading.value = true;
    try {
      const params = new URLSearchParams();

      if(name){
        params.append('name',name);
      }
      if(sku){
        params.append('sku', sku);
      }

      const response = await axios.get("/products/search", {params});
      products.value = response.data;
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
    products,
    loading,
    modalState,
    fetchProducts,
    createNewProduct,
    updateProduct,
    removeProduct,
  };
});
