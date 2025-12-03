import axios from 'axios';
import { useNotificationStore } from '@/stores/notification'; // Make sure you created this store

// 1. Create a custom instance
const api = axios.create({
  baseURL: 'http://localhost:8080', // Centralize your backend URL here
  timeout: 5000, // Fail if request takes longer than 5 seconds
  headers: {
    'Content-Type': 'application/json',
  }
});

// 2. Add the Response Interceptor
api.interceptors.response.use(
  (response) => {
    // 200 OK - Return data as is
    return response;
  },
  (error) => {
    // ERROR - Handle it globally
    const notify = useNotificationStore();
    let message = "An unexpected error occurred.";

    if (error.response) {
      // Server returned a response (4xx, 5xx)
      const status = error.response.status;
      if (status === 400) {
        message = error.response.error || "Invalid Request";
      } else if (status === 404) {
        message = "Resource not found.";
      } else if (status === 409) {
        message = error.response.error || "Conflict: An item with this SKU already exists.";
      } else if (status === 500) {
        message = "Server error. Please try again later.";
      }
    } else if (error.code === "ERR_NETWORK") {
      // Server is down
      message = "Unable to connect to server.";
    }

    // Show the Snackbar
    notify.showError(message);

    // Reject the promise so the specific component knows it failed
    return Promise.reject(error);
  }
);

export default api;
