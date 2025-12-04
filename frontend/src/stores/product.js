import { defineStore } from "pinia";
import { ref } from "vue";
import api from "@/plugins/axios";

export const useProductStore = defineStore("product", () => {
  // State
  const product = ref([]); // Kept for backward compatibility if used elsewhere
  const items = ref([]);   // Main list used in your view
  const loading = ref(true);
  const hasNext = ref(false);
  const hasPrev = ref(true);

  // Cache Container
  const pageCache = ref(new Map());

  // Search/Filter State
  const searchName = ref("");
  const searchSku = ref("");
  const pageNo = ref(1);
  const pageSize = ref(10);
  const sortBy = ref("updated_on");
  const sortDir = ref("DESC");

  // Helper: Create a unique key string based on current filter values
  const generateCacheKey = (params) => {
    return JSON.stringify(params);
  };

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

      // Generate Key
      const key = generateCacheKey(params);

      // CHECK CACHE
      if (pageCache.value.has(key)) {
        console.log("⚡ Cache Hit! Serving from Memory.");
        const cachedData = pageCache.value.get(key);

        // Restore state from cache
        items.value = cachedData.items;
        hasNext.value = cachedData.hasNext;
        hasPrev.value = (params.pageNo > 1);

        loading.value = false;
        return;
      }

      console.log("🌍 Cache Miss. Calling API...");
      const response = await api.get(`/products`, { params });

      // UPDATE STATE
      items.value = response.data.products;
      hasNext.value = response.data.hasNext;
      hasPrev.value = (params.pageNo > 1);

      // SAVE TO CACHE (The important missing part!)
      pageCache.value.set(key, {
        items: response.data.products,
        hasNext: response.data.hasNext
      });

    } catch (error) {
      console.error("Failed to fetch products:", error);
    } finally {
      loading.value = false;
    }
  }

  async function createNewProduct(productData) {
    try {
      await api.post("/products", productData);

      pageCache.value.clear();

      await fetchProducts();

      return true;

    } catch (error) {

      console.error("Error during create request:", error);

      return false;

    }
  }

  async function updateProduct(productData) {
    try {
      await api.put(`/products/${productData.id}`, productData);

      pageCache.value.clear();

      await fetchProducts();

      return true;

    } catch (error) {

      console.error("Error during update request: ", error);

      return false;
    }
  }

  async function removeProduct(productId) {
    try {
      await api.delete(`/products/${productId}`);

      pageCache.value.clear();

      await fetchProducts();

      return true;

    } catch (error) {

      console.error("Error during remove request: ", error);

      return false;

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
