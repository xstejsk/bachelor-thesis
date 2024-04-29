import axiosInstance from "./base";
import { Credentials, AuthResponse } from "../models/user";

const login = async (credentials: Credentials): Promise<AuthResponse> => {
  // read backendUrl from .env file
  const response = await axiosInstance.post(
    "/api/v1/auth/token",
    new URLSearchParams({ ...credentials, grant_type: "password" })
  );
  return response.data;
};

export default {
  login,
};
