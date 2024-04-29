import { atom } from "recoil";

export const reservationsAtom = atom<string[]>({
  key: "reservations",
  default: [],
});

export default reservationsAtom;
