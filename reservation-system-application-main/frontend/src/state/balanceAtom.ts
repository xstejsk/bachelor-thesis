import { atom } from "recoil";

const storedBalance = localStorage.getItem("balance");
const balance = storedBalance ? JSON.parse(storedBalance) : 0;
export const balanceAtom = atom<number>({
  key: "balance",
  default: balance,
});

export default balanceAtom;
