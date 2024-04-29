import axios from "axios";
import toast from "react-hot-toast";

const axiosInstance = axios.create({
  baseURL: import.meta.env.BACKEND_URL || "http://localhost:8080",
});

axiosInstance.interceptors.request.use(function (config) {
  const auth = localStorage.getItem("auth");
  const token = auth ? JSON.parse(auth).accessToken : null;
  config.headers.Authorization = token ? `Bearer ${token}` : undefined;
  return config;
});

let isRefreshing = false; // Global variable to track if refresh request is in progress

axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async function (error) {
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      if (!isRefreshing) {
        isRefreshing = true;
        import.meta.env.DEBUG && console.log("Refreshing token...");
        try {
          const access_token = await refreshAccessToken();
          isRefreshing = false; // Reset the flag after successful refresh
          axios.defaults.headers.common["Authorization"] =
            "Bearer " + access_token;
          return axiosInstance(originalRequest);
        } catch (refreshError) {
          isRefreshing = false; // Reset the flag on refresh error
          // Handle the refresh error as needed
          return Promise.reject(refreshError);
        }
      } else {
        import.meta.env.DEBUG &&
          console.log("Token refresh already in progress...");
        // Another request is already refreshing the token, wait for it to complete
        return new Promise((resolve) => {
          const interval = setInterval(() => {
            if (!isRefreshing) {
              clearInterval(interval);
              resolve(axiosInstance(originalRequest));
            }
          }, 100);
        });
      }
    }
    return Promise.reject(error);
  }
);

const refreshAccessToken = async () => {
  const auth = localStorage.getItem("auth");
  const refresh_token = auth ? JSON.parse(auth).refreshToken : null;
  try {
    const response = await axiosInstance.post(
      "/api/v1/auth/token",
      new URLSearchParams({ refresh_token, grant_type: "refresh_token" })
    );
    localStorage.setItem("auth", JSON.stringify(response.data));
    return response.data.accessToken;
  } catch (error) {
    console.log("Relace vypršela, prihláste sa prosím znova.");
    toast.error("Relace vypršela, přihlašte sa prosím znova.");
    window.location.href = "/login";
    throw error; // Rethrow the error to be caught in the interceptor
  }
};

export default axiosInstance;
