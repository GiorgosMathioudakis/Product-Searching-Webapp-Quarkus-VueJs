<script setup>
import { useProductStore } from "@/stores/product.js";
import { ref } from "vue";

const productStore = useProductStore();

const props = defineProps({
  isEditMode: Boolean,
  dialogTitle: String,
  product: {
    type: Object,
    required: true,
  },
  isVisible: Boolean,
});

const localproduct = ref({ ...props.product });

const emit = defineEmits(["close"]);

function handleCloseDialog() {
  emit("close");
}

function handleSave() {
  if (props.isEditMode) {
    console.log("edit", localproduct.value);
    productStore.updateProduct(localproduct.value);
  } else {
    console.log("create", localproduct.value);
    productStore.createNewProduct(localproduct.value);
  }

  emit("close");
}

</script>

<template>
  <v-dialog v-if="isVisible" max-width="600px" persistent>
    <v-card>
      <v-card-title class="pa-4 bg-primary">
        <span class="text-h5">{{ props.dialogTitle }}</span>
      </v-card-title>

      <v-card-text>
        <v-form>
          <v-container>
            <v-text-field
              v-model="localproduct.name"
              density="compact"
              label="Name"
              variant="outlined"
            />
            <v-text-field
              v-model="localproduct.sku"
              density="compact"
              label="SKU"
              variant="outlined"
            />
            <v-text-field
              v-model="localproduct.description"
              density="compact"
              label="Description"
              variant="outlined"
            />
            <v-text-field
              v-model.number="localproduct.price"
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
        <v-btn
          color="blue-grey-darken-1"
          variant="text"
          @click="handleCloseDialog"
        >
          Cancel
        </v-btn>
        <v-btn
          color="blue-darken-1"
          variant="flat"
          @click="handleSave"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
