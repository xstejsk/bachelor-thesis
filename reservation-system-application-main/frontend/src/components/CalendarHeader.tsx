import React from "react";
import FormModal from "./FormModal";
import Box from "@mui/joy/Box";
import ButtonGroup from "@mui/joy/ButtonGroup";
import IconButton from "@mui/joy/IconButton";
import * as FcIcons from "react-icons/fc";
import FullCalendar from "@fullcalendar/react"; // must go before plugins
import MenuButton from "@mui/joy/MenuButton";
import Menu from "@mui/joy/Menu";
import MenuItem from "@mui/joy/MenuItem";
import Dropdown from "@mui/joy/Dropdown";
import Input from "@mui/joy/Input";
import { Grid } from "@mui/joy";
import Button from "@mui/joy/Button";
import EventForm from "./forms/EventForm";
import { useRecoilValue } from "recoil";
import authAtom from "../state/authAtom";
import NewReservationForm from "./forms/NewReservationForm";
import { EventImpl } from "@fullcalendar/core/internal";
import { EventInput } from "@fullcalendar/core/index.js";
import { Role } from "../models/user";
import { useCalendarControls } from "../hooks/useCalendarControls";

interface CalendarHeaderProps {
  calendarRef: React.RefObject<FullCalendar>;
}

export type ReservationModalProps = {
  eventImpl: EventImpl | undefined;
  modalOpen: boolean;
};

const CalendarHeader = (props: CalendarHeaderProps) => {
  const [reservationModalProps, setReservationModalProps] =
    React.useState<ReservationModalProps>({
      eventImpl: undefined,
      modalOpen: false,
    });
  const auth = useRecoilValue(authAtom);
  // const reservedEventsIds = useRecoilValue(reservationsAtom);
  const {
    nextHandle,
    prevHandle,
    todayHandle,
    selectView,
    gotoDateHandle,
    title,
    date,
  } = useCalendarControls({
    calendarRef: props.calendarRef,
    setReservationModal: (props: ReservationModalProps) => {
      setReservationModalProps(props);
    },
    setViewIndex: (index: number) => setSelectedIndex(index),
  });

  const updateEventSpaces = (updatedEvent: EventInput) => {
    props.calendarRef.current
      ?.getApi()
      .getEventById(updatedEvent.id!)
      ?.remove();
    props.calendarRef.current?.getApi().addEvent(updatedEvent);
  };
  const [selectedIndex, setSelectedIndex] = React.useState<number>(1);
  const [open, setOpen] = React.useState(false);

  // props.calendarRef.current?.getApi().on("eventClick", (info) => {
  //   if (reservedEventsIds.includes(info.event.id)) {
  //     return;
  //   } else if (
  //     info.event.start! > new Date() &&
  //     info.event.extendedProps?.spacesAvailable > 0
  //   ) {
  //     setReservationModalProps({
  //       eventImpl: info.event,
  //       modalOpen: true,
  //     });
  //   }
  // });

  const toggleReservationModal = (open: boolean) => {
    setReservationModalProps({
      eventImpl: reservationModalProps.eventImpl,
      modalOpen: open,
    });
  };

  React.useEffect(() => {
    todayHandle();
  }, []);

  return (
    <React.Fragment>
      <Grid container>
        <Grid xs={12}>
          <Box
            className="calendar-header"
            sx={{
              display: "flex",
              justifyContent: "space-between",
              my: 1,
              gap: 1,
            }}
          >
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                gap: 1,
              }}
            >
              <ButtonGroup
                color="neutral"
                orientation="horizontal"
                size="md"
                variant="outlined"
              >
                <IconButton onClick={prevHandle}>
                  <FcIcons.FcPrevious style={{ color: "#319795" }} />
                </IconButton>
                <Input
                  type="date"
                  value={date}
                  onChange={(e) => {
                    gotoDateHandle(e.target.value);
                  }}
                  sx={{
                    width: "140px",
                  }}
                />

                <IconButton onClick={nextHandle}>
                  <FcIcons.FcNext />
                </IconButton>
              </ButtonGroup>
            </Box>
            <Box
              sx={{
                position: {
                  md: "absolute",
                },
                left: {
                  md: "50%",
                },
                transform: {
                  md: "translateX(-50%)",
                },
                fontWeight: "bold",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {title}
            </Box>
            <Box
              sx={{
                display: {
                  xs: "none",
                  sm: "none",
                  md: "flex",
                },
              }}
            >
              <Dropdown>
                <MenuButton>Období</MenuButton>
                <Menu>
                  <MenuItem
                    {...(selectedIndex === 0 && {
                      selected: true,
                      variant: "soft",
                    })}
                    onClick={selectView(0)}
                  >
                    Den
                  </MenuItem>
                  <MenuItem
                    selected={selectedIndex === 1}
                    onClick={selectView(1)}
                  >
                    Týden
                  </MenuItem>
                  <MenuItem
                    selected={selectedIndex === 2}
                    onClick={selectView(2)}
                  >
                    Měsíc
                  </MenuItem>
                </Menu>
              </Dropdown>
            </Box>
          </Box>
        </Grid>
        {auth?.user.role === "ADMIN" && (
          <Grid
            sx={{
              padding: 1,
            }}
          >
            <Button onClick={() => setOpen(true)}>Přidat událost</Button>
          </Grid>
        )}
      </Grid>
      <FormModal
        open={open}
        setOpen={setOpen}
        title={"Nová událost"}
        description={"Vyplňte formulář pro vytvoření nové události."}
        challenge={""}
        form={
          <EventForm
            withSubmit={() => {
              setOpen(false);
              props.calendarRef.current?.getApi().refetchEvents();
            }}
          />
        }
      />

      <FormModal
        open={reservationModalProps.modalOpen}
        setOpen={(open) => toggleReservationModal(open)}
        title="Rezervace"
        description={
          reservationModalProps.eventImpl?.extendedProps["description"]
        }
        challenge={
          auth.user.role !== Role.GUEST
            ? `Přejete si přihlásit se na událost ${
                reservationModalProps.eventImpl?.title ?? ""
              }?`
            : "Pro vytvoření rezervace se přihlašte."
        }
        // description=""
        form={
          <NewReservationForm
            closeModal={() => {
              setReservationModalProps({
                ...reservationModalProps,
                modalOpen: false,
              });
            }}
            refetchEvents={() => {
              props.calendarRef.current?.getApi().refetchEvents();
            }}
            event={reservationModalProps.eventImpl}
            updateEventSpaces={updateEventSpaces}
          />
        }
      />
    </React.Fragment>
  );
};

export default CalendarHeader;
