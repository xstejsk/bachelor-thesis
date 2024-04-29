import axiosInstance from "./base";
import { NewUser, User } from "../models/user";
import { Page } from "../models/common";
import { Role } from "../models/user";

type UpdateRoleParams = {
  id: string;
  role: Role;
};

export type UpdateBalanceParams = {
  id: string;
  balance: number;
};

type ChangePasswordParams = {
  id: string;
  oldPassword: string;
  newPassword: string;
};

type UpdateDiscounParams = {
  id: string;
  discountActive: boolean;
};

const registerUser = async (user: NewUser): Promise<User> => {
  // read backendUrl from .env file

  const response = await axiosInstance.post("/api/v1/users", user);
  return response.data;
};

const verifyUser = async (token: string): Promise<User> => {
  const response = await axiosInstance.put("/api/v1/users/verification", {
    token,
  });
  return response.data;
};

const getAll = async (params: URLSearchParams): Promise<Page<User>> => {
  const response = await axiosInstance.get("/api/v1/users", {
    params,
  });
  return response.data;
};

const updateRole = async ({ id, role }: UpdateRoleParams): Promise<User> => {
  import.meta.env.DEBUG && console.log(`/api/v1/users/${id}/role`);
  import.meta.env.DEBUG && console.log({ role });
  const response = await axiosInstance.put(`/api/v1/users/${id}/role`, {
    role,
  });

  return response.data;
};

const resetPassword = async (email: string): Promise<void> => {
  await axiosInstance.post("/api/v1/users/password", {
    email,
  });
};

const updateUsersDiscountStatus = async ({
  id,
  discountActive,
}: UpdateDiscounParams): Promise<User> => {
  const response = await axiosInstance.put(`/api/v1/users/${id}/discount`, {
    discountActive,
  });

  return response.data;
};

const changePassword = async ({
  id,
  oldPassword,
  newPassword,
}: ChangePasswordParams): Promise<User> => {
  const response = await axiosInstance.put(`/api/v1/users/${id}/password`, {
    oldPassword,
    newPassword,
  });

  return response.data;
};

const updateBalance = async ({
  id,
  balance,
}: UpdateBalanceParams): Promise<User> => {
  const response = await axiosInstance.put(`/api/v1/users/${id}/balance`, {
    balance,
  });

  return response.data;
};

const verifyPasswordReset = async (token: string): Promise<void> => {
  const response = await axiosInstance.put("/api/v1/users/password", {
    token,
  });

  return response.data;
};

export default {
  registerUser,
  verifyUser,
  getAll,
  updateRole,
  updateBalance,
  changePassword,
  resetPassword,
  verifyPasswordReset,
  updateUsersDiscountStatus,
};
