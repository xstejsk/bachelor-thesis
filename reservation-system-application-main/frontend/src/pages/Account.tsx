import React from "react";
import { useRecoilState } from "recoil";
import authAtom from "../state/authAtom";
import { Box, Card } from "@mui/joy";
import { Stack } from "@mui/joy";
import Button from "@mui/joy/Button";
import CardActions from "@mui/joy/CardActions";
import { AxiosError } from "axios";
import { FormLabel } from "@mui/joy";
import { FormControl } from "@mui/joy";
import { Input } from "@mui/joy";
import { Typography } from "@mui/joy";
import { Divider } from "@mui/joy";
import FormModal from "../components/FormModal";
import ChangePasswordForm from "../components/forms/ChangePasswordForm";
import usersApi from "../service/usersApi";
import toast from "react-hot-toast";
import { useMutation } from "@tanstack/react-query";

const Account = () => {
  const [auth, setAuth] = useRecoilState(authAtom);
  const [open, setOpen] = React.useState(false);
  const isDiscountActive = auth.user.hasDailyDiscount;

  const { mutate: updateDiscountStatus } = useMutation({
    mutationFn: usersApi.updateUsersDiscountStatus,
    onSuccess: () => {
      setAuth((prev) => ({
        ...prev,
        user: {
          ...prev.user,
          hasDailyDiscount: !prev.user.hasDailyDiscount,
        },
      }));
      toast.success(
        isDiscountActive
          ? "Karta Multisport byla zrušena"
          : "Karta Multisport byla přidána"
      );
    },
    onError: (error: AxiosError) => {
      toast.error(
        isDiscountActive
          ? "Kartu Multisport se nepodařilo zrušit"
          : "Kartu Multisport se nepodařilo přidat"
      );
      import.meta.env.DEBUG && console.log(error);
    },
  });

  return (
    <React.Fragment>
      <Box
        className="main"
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "flex-start",
        }}
      >
        <Card>
          <Box sx={{ mb: 1 }}>
            <Typography level="title-md">Můj účet</Typography>
            <Typography level="body-sm">Vaše osobní informace</Typography>
          </Box>
          <Divider />

          <Stack direction="row" spacing={1} sx={{ display: "flex", my: 1 }}>
            <Stack spacing={2} sx={{ flexGrow: 1 }}>
              <Stack spacing={1}>
                <FormLabel>Jméno</FormLabel>
                <FormControl>
                  <Input
                    size="sm"
                    placeholder="First name"
                    disabled
                    defaultValue={auth.user.firstName}
                  />
                </FormControl>
                <FormLabel>Příjmení</FormLabel>
                <FormControl>
                  <Input
                    size="sm"
                    placeholder="Last name"
                    sx={{ flexGrow: 1 }}
                    disabled
                    defaultValue={auth.user.lastName}
                  />
                </FormControl>
                <FormLabel>Email</FormLabel>
                <FormControl>
                  <Input
                    size="sm"
                    placeholder="Email"
                    disabled
                    defaultValue={auth.user.email}
                  />
                </FormControl>
              </Stack>
            </Stack>
          </Stack>
          <CardActions>
            <Stack spacing={1} width={"100%"}>
              <Button
                variant="solid"
                size="sm"
                onClick={() =>
                  updateDiscountStatus({
                    id: auth.user.id,
                    discountActive: !auth.user.hasDailyDiscount,
                  })
                }
                type="button"
              >
                {auth.user.hasDailyDiscount
                  ? "Již nemám kartu Multisport"
                  : "Mám kartu Multisport"}
              </Button>
              <Button variant="solid" size="sm" onClick={() => setOpen(true)}>
                Změnit heslo
              </Button>
            </Stack>
          </CardActions>
        </Card>
      </Box>
      <FormModal
        open={open}
        setOpen={setOpen}
        title={"Změna hesla"}
        description={undefined}
        challenge={undefined}
        form={<ChangePasswordForm closeModal={() => setOpen(false)} />}
      />
    </React.Fragment>
  );
};

export default Account;
