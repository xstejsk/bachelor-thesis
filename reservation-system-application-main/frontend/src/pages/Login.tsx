import Sheet from "@mui/joy/Sheet";
import Typography from "@mui/joy/Typography";
import { Link } from "react-router-dom";
import LoginForm from "../components/forms/LoginForm";
import { useNavigate } from "react-router";
import { useEffect } from "react";
import { useLogout } from "../hooks/useLogout";
import { Stack } from "@mui/joy";

export default function Login() {
  const navigate = useNavigate();
  const { logout } = useLogout();

  useEffect(() => {
    logout();
  }, []);

  return (
    <main className="main">
      <Sheet
        sx={{
          width: 300,
          mx: "auto", // margin left & right
          my: 4, // margin top & bottom
          py: 3, // padding top & bottom
          px: 2, // padding left & right
          display: "flex",
          flexDirection: "column",
          gap: 2,
          borderRadius: "sm",
          boxShadow: "md",
        }}
        variant="outlined"
      >
        <div>
          <Typography level="h4" component="h1">
            <b>Vítejte!</b>
          </Typography>
          <Typography level="body-sm">Pro pokračování se přihlašte.</Typography>
        </div>
        <LoginForm withSubmit={() => navigate("/calendars")} />
        <Stack gap={"0.5rem"}>
          <Typography
            endDecorator={
              <Link to="/register" className="basic-link">
                Registrace
              </Link>
            }
            fontSize="sm"
            sx={{ alignSelf: "center" }}
          >
            Nemáte účet?
          </Typography>
          <Typography
            endDecorator={
              <Link to="/forgot-password" className="basic-link">
                Obnovit heslo
              </Link>
            }
            fontSize="sm"
            sx={{ alignSelf: "center" }}
          >
            Zapomněli jste heslo?
          </Typography>
        </Stack>
      </Sheet>
    </main>
  );
}
