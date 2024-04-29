import React from "react";
import { useSearchParams } from "react-router-dom";
import Alert from "@mui/joy/Alert";
import Typography from "@mui/joy/Typography";
import * as AiIcons from "react-icons/ai";
import { Link } from "react-router-dom";
import { Box } from "@mui/joy";

type TokenConfirmationProps = {
  submitToken: (token: string) => Promise<void | object>;
};

const TokenConfirmation = ({ submitToken }: TokenConfirmationProps) => {
  const [searchParams] = useSearchParams();
  const [success, setSuccess] = React.useState<undefined | boolean>(undefined);

  React.useEffect(() => {
    if (!searchParams.has("token")) {
      import.meta.env.DEBUG && console.log("token is null");
      return;
    }
    submitToken(searchParams.get("token") ?? "")
      .then(() => {
        setSuccess(true);
      })
      .catch((err) => {
        import.meta.env.DEBUG && console.log(err);
        setSuccess(false);
      });
  }, []);

  // Conditional rendering based on the value of `success`
  return (
    <Box className="main">
      {success === undefined ? (
        <></>
      ) : success ? (
        <Alert
          sx={{ alignItems: "flex-start" }}
          startDecorator={<AiIcons.AiFillCheckCircle />}
          variant="soft"
          color={"success"}
        >
          <div>
            <Typography level="title-lg">Potvrzení úspěšné</Typography>
            <Typography level="body-md" color={"success"}>
              Nyní se můžete{" "}
              <Link to="/login" className="basic-link">
                přihlásit
              </Link>
            </Typography>
          </div>
        </Alert>
      ) : (
        <Alert
          sx={{ alignItems: "flex-start" }}
          startDecorator={<AiIcons.AiFillCheckCircle />}
          variant="soft"
          color={"danger"}
        >
          <div>
            <Typography level="title-lg">Potvrzení neúspěšné</Typography>
            <Typography level="body-md" color={"danger"}>
              Odkaz je neplatný nebo již byl použit
            </Typography>
          </div>
        </Alert>
      )}
    </Box>
  );
};

export default TokenConfirmation;
