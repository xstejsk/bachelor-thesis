import { useRecoilState } from "recoil";
import balanceAtom from "../state/balanceAtom";

const useUsersBalance = () => {
  const [currentBalance, setBalance] = useRecoilState(balanceAtom);
  const updateCurrentBalance = (amount: number) => {
    localStorage.setItem("balance", JSON.stringify(amount));
    setBalance(amount);
  };
  return { currentBalance, updateCurrentBalance };
};

export default useUsersBalance;
