import { atom } from "recoil";
import { AuthResponse, Role } from "../models/user";

const storedUser = localStorage.getItem("auth");
const user = storedUser
  ? JSON.parse(storedUser)
  : {
      user: {
        id: "",
        firstName: "",
        lastName: "",
        email: "",
        balance: 0,
        enabled: true,
        locked: false,
        role: Role.GUEST,
      },
      accessToken: "",
      refreshToken: "",
    };

export const authAtom = atom<AuthResponse>({
  key: "auth",
  default: user,
});

export default authAtom;
