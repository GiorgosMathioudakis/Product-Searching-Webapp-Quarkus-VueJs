<script setup>
import { useProductStore } from "@/stores/product.js";

const productStore = useProductStore();

const props = defineProps({
  dialogTitle: {
    type: String,
    required: true,
  },
  productForm: {
    required: true,
  },
});

function closeDialog() {
  dialogVisible.value = false;
}
</script>

<template>
  <v-dialog max-width="600px" persistent>
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
          @click="productStore.saveProduct(productForm)"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
