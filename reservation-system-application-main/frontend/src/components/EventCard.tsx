import React from "react";
import { EventContentArg } from "@fullcalendar/core/index.js";
import { Stack, Tooltip } from "@mui/joy";
import Typography from "@mui/joy/Typography";
import format from "date-fns/format";
import * as BiIcons from "react-icons/bi";
import * as FaIcons from "react-icons/fa";
import * as BsIcons from "react-icons/bs";
import { EventExtendedProps } from "../models/calendar";
import { useRecoilValue } from "recoil";
import reservationsAtom from "../state/reservationsAtom";
import { Chip } from "@mui/joy";

const DAY_GRID_MONTH = "dayGridMonth";
const TIME_GRID_DAY = "timeGridDay";
const TIME_GIRD_WEEK = "timeGridWeek";

type EventCardProps = {
  eventInfo: EventContentArg;
};

const EventCard = (props: EventCardProps) => {
  const extendedProps = props.eventInfo.event
    .extendedProps as EventExtendedProps;
  const viewType = props.eventInfo.view.type;
  const startTime = format(props.eventInfo.event.start!, "HH:mm");
  const endTime = format(props.eventInfo.event.end!, "HH:mm");
  const reservedEventsIds = useRecoilValue(reservationsAtom);

  if (viewType === TIME_GRID_DAY || viewType === TIME_GIRD_WEEK)
    return (
      <Stack
        spacing={1}
        direction={"column"}
        sx={{
          px: 0.5,
          height: "100%",
          width: "100%",
        }}
        justifyContent={"space-between"}
      >
        <Stack
          direction={"column"}
          sx={{
            alignItems: "flex-start",
          }}
        >
          <Typography
            sx={{
              color: "white",
            }}
            level="title-sm"
          >
            {props.eventInfo.event.title}
          </Typography>
          {reservedEventsIds.includes(props.eventInfo.event.id) && (
            <Typography
              sx={{
                color: "white",
                paddingRight: 0.5,
                paddingTop: 0.5,
              }}
              level="body-sm"
              component={"div"}
              position={"absolute"}
              fontWeight={"bold"}
              top={0}
              right={0}
            >
              <Chip
                variant="solid"
                sx={{
                  // backgroundColor: "#ffd1dc",
                  backgroundColor: "white",
                  color: "primary.solidBg",
                }}
              >
                Přihlášen
              </Chip>
            </Typography>
          )}

          <Typography
            sx={{
              color: "white",
            }}
            startDecorator={<BiIcons.BiTime />}
            level="body-xs"
          >{`${startTime}-${endTime}`}</Typography>
        </Stack>
        <Stack
          direction={"row"}
          sx={{
            alignItems: "center",
          }}
          justifyContent={"space-between"}
        >
          <React.Fragment>
            <Stack
              direction={"row"}
              sx={{
                alignItems: "center",
              }}
              spacing={1}
            >
              <Typography
                sx={{
                  color: "white",
                }}
                startDecorator={<FaIcons.FaCoins />}
                endDecorator={"Kr."}
                level="body-xs"
              >
                {extendedProps.price}
              </Typography>
              <Typography
                sx={{
                  color: "white",
                }}
                level="body-xs"
              >
                /
              </Typography>
              <Typography
                sx={{
                  color: "white",
                }}
                startDecorator={<BsIcons.BsFillPersonVcardFill />}
                endDecorator={"Kr."}
                level="body-xs"
              >
                {extendedProps.discountPrice}
              </Typography>
            </Stack>

            <Typography
              sx={{
                color: "white",
              }}
              startDecorator={<BsIcons.BsPerson />}
              level="body-xs"
            >
              {`${
                extendedProps.maximumCapacity - extendedProps.spacesAvailable
              }/${extendedProps.maximumCapacity}`}
            </Typography>
          </React.Fragment>
        </Stack>
      </Stack>
    );

  if (viewType === DAY_GRID_MONTH)
    return (
      <Stack
        spacing={1}
        sx={{
          px: 0.5,
        }}
      >
        <Typography
          sx={{
            color: "white",
          }}
          level="title-md"
        >
          {props.eventInfo.event.title}
        </Typography>
        {reservedEventsIds.includes(props.eventInfo.event.id) && (
          <Typography
            sx={{
              color: "white",
            }}
            level="title-md"
            component={"div"}
            position={"absolute"}
            fontWeight={"bold"}
            top={0}
            right={0}
            paddingRight={1}
          >
            <Chip
              variant="solid"
              sx={{
                // backgroundColor: "#ffd1dc",
                backgroundColor: "white",
                color: "primary.solidBg",
              }}
            >
              Přihlášen
            </Chip>
          </Typography>
        )}
        <React.Fragment>
          <Tooltip title="Čas události">
            <Typography
              sx={{
                color: "white",
              }}
              startDecorator={<BiIcons.BiTime />}
              level="body-sm"
            >{`${startTime}-${endTime}`}</Typography>
          </Tooltip>
          <Stack
            direction={"row"}
            sx={{
              alignItems: "center",
            }}
            spacing={1}
          >
            <Tooltip title="Běžná cena">
              <Typography
                sx={{
                  color: "white",
                }}
                startDecorator={<FaIcons.FaCoins />}
                endDecorator={"Kr."}
                level="body-sm"
              >
                {extendedProps.price}
              </Typography>
            </Tooltip>
            <Typography
              sx={{
                color: "white",
              }}
              level="body-sm"
            >
              /
            </Typography>

            <Tooltip title="Cena s kartou Multisport">
              <Typography
                sx={{
                  color: "white",
                }}
                startDecorator={<BsIcons.BsFillPersonVcardFill />}
                endDecorator={"Kr."}
                level="body-sm"
              >
                {extendedProps.discountPrice}
              </Typography>
            </Tooltip>
          </Stack>
          <Tooltip title="Obsazenost">
            <Typography
              sx={{
                color: "white",
              }}
              startDecorator={<BsIcons.BsPerson />}
              level="body-sm"
            >
              {`${
                extendedProps.maximumCapacity - extendedProps.spacesAvailable
              }/${extendedProps.maximumCapacity}`}
            </Typography>
          </Tooltip>
        </React.Fragment>
      </Stack>
    );
};

export default EventCard;
