import axiosInstance from "./base";
import { Event } from "../models/calendar";
import { Page } from "../models/common";

export type UpdateEventParams = {
  id: string;
  data: {
    title: string;
    description: string;
    updateSeries: boolean;
  };
};

const getAll = async (params: URLSearchParams): Promise<Page<Event>> => {
  const response = await axiosInstance.get(`/api/v1/events`, {
    params,
  });
  return response.data;
};

const deleteEvent = async (id: string): Promise<Event> => {
  const response = await axiosInstance.delete(`/api/v1/events/${id}`);
  return response.data;
};

const updateEvent = async ({ id, data }: UpdateEventParams): Promise<Event> => {
  const response = await axiosInstance.patch(`/api/v1/events/${id}`, data);
  return response.data;
};

export default {
  getAll,
  deleteEvent,
  updateEvent,
};
