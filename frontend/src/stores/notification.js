import { defineStore } from "pinia";
import { ref } from "vue";

export const useNotificationStore = defineStore("notification", () => {
  const show = ref(false);
  const message = ref("");
  const color = ref("error"); // default to red

  function showError(msg) {
    message.value = msg;
    color.value = "error";
    show.value = true;
  }

  function showSuccess(msg) {
    message.value = msg;
    color.value = "success";
    show.value = true;
  }

  return { show, message, color, showError, showSuccess };
});
