import axiosInstance from "./base";
import { Reservation } from "../models/calendar";
import { Page } from "../models/common";

const getAll = async (params: URLSearchParams): Promise<Page<Reservation>> => {
  // filter all fields from params that are null
  import.meta.env.DEBUG && console.log(params);
  const response = await axiosInstance.get(`/api/v1/reservations`, {
    params,
  });
  return response.data;
};

const deleteReservation = async (id: string): Promise<Reservation> => {
  const response = await axiosInstance.delete(`/api/v1/reservations/${id}`);
  return response.data;
};

export default {
  getAll,
  deleteReservation,
};
