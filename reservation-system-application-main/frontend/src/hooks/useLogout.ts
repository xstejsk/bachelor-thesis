import { useRecoilState } from "recoil";
import authAtom from "../state/authAtom";
import reservationsAtom from "../state/reservationsAtom";
import { defaultUser } from "../models/user";

export const useLogout = () => {
  const [, setAuth] = useRecoilState(authAtom);
  const [, setReservations] = useRecoilState(reservationsAtom);
  const logout = () => {
    setAuth({
      user: defaultUser,
      accessToken: "",
      refreshToken: "",
    });
    localStorage.removeItem("auth");
    setReservations([]);
  };
  return { logout };
};
