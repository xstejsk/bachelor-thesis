import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import { Input } from "@mui/joy";
import Button from "@mui/joy/Button";
import usersApi from "../../service/usersApi";
import { toast } from "react-hot-toast";
import { useForm } from "react-hook-form";

const ResetPasswordForm = () => {
  const { register, handleSubmit, reset } = useForm<{
    email: string;
  }>();

  const onSubmit = (data: { email: string }) => {
    import.meta.env.DEBUG && console.log(data);
    usersApi
      .resetPassword(data.email)
      .then(() => {
        toast.success("Pokud email existuje, bylo zasláno nové heslo.");
      })
      .catch((err) => {
        toast.error("Něco se pokazilo.");
        import.meta.env.DEBUG && console.log(err);
      });
    reset();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <FormControl>
        <FormLabel>Email, kam bude zasláno nové heslo</FormLabel>
        <Input
          // html input attribute
          required
          {...register("email")}
          type="email"
          placeholder="johndoe@email.com"
        />
      </FormControl>
      <Button sx={{ mt: 1 /* margin top */, width: "100%" }} type="submit">
        Zaslat heslo
      </Button>
    </form>
  );
};

export default ResetPasswordForm;
