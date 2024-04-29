import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import { Input } from "@mui/joy";
import Button from "@mui/joy/Button";
import { zodResolver } from "@hookform/resolvers/zod";
import { credentialsSchema } from "./schemas";
import authApi from "../../service/authApi";
import { toast } from "react-hot-toast";
import { useForm } from "react-hook-form";
import { AuthResponse, Credentials } from "../../models/user";
import authAtom from "../../state/authAtom";
import { useSetRecoilState } from "recoil";
import useUsersBalance from "../../hooks/useUsersBalance";

type LoginFormProps = {
  withSubmit: () => void;
};

const LoginForm = (props: LoginFormProps) => {
  const setAuth = useSetRecoilState(authAtom);
  const { register, handleSubmit, reset } = useForm<Credentials>({
    resolver: zodResolver(credentialsSchema),
  });
  const { updateCurrentBalance } = useUsersBalance();

  const onSubmit = (data: Credentials) => {
    import.meta.env.DEBUG && console.log(data);
    authApi
      .login(data)
      .then((res) => {
        setAuth(res);
        localStorage.setItem("auth", JSON.stringify(res as AuthResponse));
        updateCurrentBalance(res.user.balance);
        props.withSubmit();
      })
      .catch((err) => {
        if (err.response?.status === 400)
          toast.error("Nepatná kombinace přihlašovacích údajů.");
        else if (err.response?.status === 403)
          toast.error("Účet není potvrzený, zkontrolujte si email.");
        else toast.error("Něco se pokazilo.");
      });
    reset();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <FormControl>
        <FormLabel>Email</FormLabel>
        <Input
          // html input attribute
          required
          {...register("username")}
          type="email"
          placeholder="johndoe@email.com"
        />
      </FormControl>
      <FormControl>
        <FormLabel>Heslo</FormLabel>
        <Input
          // html input attribute
          required
          {...register("password")}
          type="password"
          placeholder="heslo"
        />
      </FormControl>

      <Button sx={{ mt: 1 /* margin top */, width: "100%" }} type="submit">
        Přihlásit
      </Button>
    </form>
  );
};

export default LoginForm;
