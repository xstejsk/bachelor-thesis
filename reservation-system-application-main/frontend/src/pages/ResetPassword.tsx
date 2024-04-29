import Sheet from "@mui/joy/Sheet";
import ResetPasswordForm from "../components/forms/ResetPasswordForm";

export default function ResetPassword() {
  return (
    <main className="main">
      <Sheet
        sx={{
          width: 500,
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
        <ResetPasswordForm />
      </Sheet>
    </main>
  );
}
