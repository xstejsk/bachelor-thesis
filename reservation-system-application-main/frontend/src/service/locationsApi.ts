import { Location } from "../models/calendar";
import axiosInstance from "./base";

const getAll = async (): Promise<Location[]> => {
  const response = await axiosInstance.get("/api/v1/locations");
  return response.data;
};

export default {
  getAll,
};
