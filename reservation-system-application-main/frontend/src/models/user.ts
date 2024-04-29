export type NewUser = UserBase & {
  confirmPassword: string;
  password: string;
};

export type Credentials = {
  username: string;
  password: string;
};

export enum Role {
  ADMIN = "ADMIN",
  USER = "USER",
  GUEST = "GUEST",
}

type UserBase = {
  firstName: string;
  lastName: string;
  balance: number;
  email: string;
  hasDailyDiscount: boolean;
};

export type AuthResponse = {
  user: User;
  accessToken: string;
  refreshToken: string;
};

export type User = UserBase & {
  id: string;
  enabled: boolean;
  role: Role;
  locked: boolean;
};

export const defaultUser: User = {
  id: "",
  firstName: "",
  lastName: "",
  email: "",
  balance: 0,
  enabled: true,
  locked: false,
  role: Role.GUEST,
  hasDailyDiscount: false,
};
