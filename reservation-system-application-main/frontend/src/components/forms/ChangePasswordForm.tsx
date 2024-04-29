import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Grid from "@mui/joy/Grid";
import { useForm } from "react-hook-form";
import FormHelperText from "@mui/joy/FormHelperText";
import { Box } from "@mui/joy";
import { zodResolver } from "@hookform/resolvers/zod";
import { changePasswordSchema } from "./schemas";
import usersApi from "../../service/usersApi";
import { toast } from "react-hot-toast";
import authAtom from "../../state/authAtom";
import { useRecoilValue } from "recoil";

type Passwords = {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
};

type ChangePasswordFormProps = {
  closeModal: () => void;
};

const ChangePasswordForm = (props: ChangePasswordFormProps) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
  } = useForm<Passwords>({
    resolver: zodResolver(changePasswordSchema),
  });
  const auth = useRecoilValue(authAtom);

  const onSubmit = (data: Passwords) => {
    usersApi
      .changePassword({
        id: auth.user.id,
        oldPassword: data.oldPassword,
        newPassword: data.newPassword,
      })
      .then(() => {
        toast.success("Heslo bylo úspěšně změněno.");
        props.closeModal();
        reset();
      })
      .catch((err) => {
        if (err.response.status === 400) {
          setError("oldPassword", {
            type: "manual",
            message: "Špatné staré heslo.",
          });

          import.meta.env.DEBUG && console.log(err.response.data);
        } else {
          toast.error("Heslo se nepodařilo změnit.");
          reset();
          props.closeModal();
        }
        import.meta.env.DEBUG && console.log(err);
      });
    import.meta.env.DEBUG && console.log(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Grid container spacing={2}>
        <Grid xs={12}>
          <FormControl>
            <FormLabel>Staré heslo</FormLabel>
            <Input
              type="password"
              placeholder="heslo"
              required
              {...register("oldPassword", { required: true })}
            />
          </FormControl>
          <FormHelperText
            sx={{
              color: "red",
            }}
          >
            {errors.oldPassword?.message}
          </FormHelperText>
        </Grid>

        <Grid xs={12}>
          <FormControl error={!!errors.newPassword}>
            <FormLabel>Nové heslo</FormLabel>
            <Input
              error={!!errors.newPassword}
              type="password"
              placeholder="heslo"
              {...register("newPassword", { required: true })}
            />
          </FormControl>
          <FormHelperText>{errors.newPassword?.message}</FormHelperText>
        </Grid>
        <Grid xs={12}>
          <FormControl error={!!errors.confirmPassword}>
            <FormLabel>Heslo pro kontrolu</FormLabel>
            <Input
              error={!!errors.confirmPassword}
              type="password"
              placeholder="heslo"
              {...register("confirmPassword", { required: true })}
            />
            <FormHelperText>{errors.confirmPassword?.message}</FormHelperText>
          </FormControl>
        </Grid>
      </Grid>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: 2,
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Button sx={{ mt: 1 /* margin top */, width: "100%" }} type="submit">
          Potvrdit
        </Button>
      </Box>
    </form>
  );
};

export default ChangePasswordForm;
