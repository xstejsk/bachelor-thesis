import { useForm } from "react-hook-form";
import { Box, Button } from "@mui/joy";
import { EventImpl } from "@fullcalendar/core/internal";
import calendarsApi from "../../service/calendarsApi";
import authAtom from "../../state/authAtom";
import { useRecoilValue } from "recoil";
import { useSetRecoilState } from "recoil";
import { useParams } from "react-router";
import { toast } from "react-hot-toast";
import { EventInput } from "@fullcalendar/core/index.js";
import { Role } from "../../models/user";
import reservationsAtom from "../../state/reservationsAtom";
import useUsersBalance from "../../hooks/useUsersBalance";

type NewResservationFormProps = {
  closeModal: () => void;
  refetchEvents: () => void;
  event: EventImpl | undefined;
  updateEventSpaces: (updatedEvent: EventInput) => void;
};

const NewReservationForm = (props: NewResservationFormProps) => {
  const { handleSubmit } = useForm({});
  const auth = useRecoilValue(authAtom);
  const { calendarId } = useParams<{ calendarId: string }>();
  const setReservations = useSetRecoilState(reservationsAtom);
  const { updateCurrentBalance } = useUsersBalance();

  const onSubmit = () => {
    calendarsApi
      .createReservation(calendarId ?? "", props.event?.id ?? "")
      .then((response) => {
        updateCurrentBalance(response.owner.balance);
        toast.success("Přihlášení na událost proběhlo úspěšně.");
        setReservations((reservations) => [...reservations, response.event.id]);
      })
      .catch((error) => {
        import.meta.env.DEBUG && console.log(error);
        if (error.response.status === 400) {
          toast.error("Na událost se již nelze přihlásit.");
        } else if (error.response.status === 403) {
          toast.error("Nemáte dostatečný kredit.");
        } else if (error.response.status === 409) {
          toast.error("Na událost již máte rezervaci.");
        } else {
          toast.error("Nepodařilo se přihlásit na událost.");
          import.meta.env.DEBUG && console.log("refetching events");
          props.refetchEvents();
        }
      });
    props.closeModal();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Box
        sx={{
          display: "flex",
          flexDirection: "row",
          gap: 2,
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Button
          sx={{ mt: 1, width: "100%" }}
          type="button"
          onClick={props.closeModal}
          color={"neutral"}
        >
          Zrušit
        </Button>
        <Button
          sx={{ mt: 1, width: "100%" }}
          type="submit"
          disabled={auth?.user.role === Role.GUEST}
          // onClick={props.withSubmit}
        >
          Rezervovat
        </Button>
      </Box>
    </form>
  );
};

export default NewReservationForm;
