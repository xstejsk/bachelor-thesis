import Typography from "@mui/joy/Typography";
import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import { Link } from "react-router-dom";
import Grid from "@mui/joy/Grid";
import { useForm } from "react-hook-form";
import { NewUser } from "../../models/user";
import FormHelperText from "@mui/joy/FormHelperText";
import { Box } from "@mui/joy";
import { zodResolver } from "@hookform/resolvers/zod";
import { newUserSchema } from "./schemas";
import usersApi from "../../service/usersApi";
import { toast } from "react-hot-toast";
import Checkbox from "@mui/joy/Checkbox";

const RegisterForm = () => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<NewUser>({
    resolver: zodResolver(newUserSchema),
  });

  const onSubmit = (data: NewUser) => {
    usersApi
      .registerUser(data)
      .then(() => {
        toast.success("Potvrďte registraci na uvedené emailové adrese.");
      })
      .catch((err) => {
        if (err.response.status === 409) {
          toast.error("Uživatel s tímto emailem již existuje.");
          import.meta.env.DEBUG && console.log(err.response.data);
        }
        import.meta.env.DEBUG && console.log(err);
      });

    reset();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div>
        <Typography level="h4" component="h1">
          <b>Registrace</b>
        </Typography>
        <Typography level="body-sm">Vytvořte si nový účet.</Typography>
      </div>
      <Grid container spacing={2}>
        <Grid xs={12} sm={6}>
          <FormControl error={!!errors.firstName}>
            <FormLabel>Jméno</FormLabel>
            <Input
              type="text"
              placeholder="John"
              {...register("firstName", { required: true })}
              error={!!errors.firstName}
              required
            />
            <FormHelperText>{errors.firstName?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12} sm={6}>
          <FormControl error={!!errors.lastName}>
            <FormLabel>Příjmení</FormLabel>
            <Input
              type="text"
              placeholder="Doe"
              error={!!errors.firstName}
              {...register("lastName", { required: true })}
              required
            />
            <FormHelperText>{errors.lastName?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12}>
          <FormControl error={!!errors.email}>
            <FormLabel>Email</FormLabel>
            <Input
              type="email"
              placeholder="johndoe@email.com"
              error={!!errors.email}
              {...register("email", { required: true })}
              required
            />
          </FormControl>
          <FormHelperText>{errors.email?.message}</FormHelperText>
        </Grid>

        <Grid xs={12}>
          <FormControl error={!!errors.password?.message}>
            <FormLabel>Heslo</FormLabel>
            <Input
              error={!!errors.password}
              type="password"
              placeholder="heslo"
              {...register("password", { required: true })}
            />
          </FormControl>
          <FormHelperText>{errors.password?.message}</FormHelperText>
        </Grid>
        <Grid xs={12}>
          <FormControl error={!!errors.confirmPassword}>
            <FormLabel>Heslo pro kontrolu</FormLabel>
            <Input
              error={!!errors.confirmPassword}
              type="password"
              placeholder="heslo"
              {...register("confirmPassword", { required: true })}
              required
            />
            <FormHelperText>{errors.confirmPassword?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12}>
          <FormControl>
            <Checkbox
              {...register("hasDailyDiscount", { required: true })}
              label="Vlastním kartu Multisport"
            />
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
          Vytvořit účet
        </Button>
        <Typography
          endDecorator={
            <Link to="/login" className="basic-link">
              Přihlašte se
            </Link>
          }
          fontSize="sm"
          sx={{ alignSelf: "center" }}
        >
          Již máte účet?
        </Typography>
      </Box>
    </form>
  );
};

export default RegisterForm;
