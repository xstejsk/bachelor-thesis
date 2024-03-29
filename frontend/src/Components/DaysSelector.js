import React, { useState } from "react";
import { withStyles } from "@material-ui/core/styles";
import ToggleButton from "@material-ui/lab/ToggleButton";
import ToggleButtonGroup from "@material-ui/lab/ToggleButtonGroup";

const DAYS = [
  {
    key: "MONDAY",
    label: "Po",
  },
  {
    key: "TUESDAY",
    label: "Út",
  },
  {
    key: "WEDNESDAY",
    label: "St",
  },
  {
    key: "THURSDAY",
    label: "Čt",
  },
  {
    key: "FRIDAY",
    label: "Pá",
  },
  {
    key: "SATURDAY",
    label: "So",
  },
  {
    key: "SUNDAY",
    label: "Ne",
  },
];

const StyledToggleButtonGroup = withStyles((theme) => ({
  grouped: {
    margin: theme.spacing(0.5),
    padding: theme.spacing(1, 0),
    "&:not(:first-child)": {
      border: "1px solid",
      borderColor: "#1674f7",
      borderRadius: "50%",
    },
    "&:first-child": {
      border: "1px solid",
      borderColor: "#1674f7",
      borderRadius: "50%",
    },
  },
}))(ToggleButtonGroup);

const StyledToggle = withStyles({
  root: {
    color: "#1674f7",
    "&$selected": {
      color: "white",
      background: "#1674f7",
    },
    "&:hover": {
      color: "1665f7",
      borderColor: "#9bc2fa",
      background: "#9bc2fa",
    },
    "&:hover$selected": {
      color: "1665f7",
      borderColor: "#9bc2fa",
      background: "#9bc2fa",
    },
    minWidth: 32,
    maxWidth: 32,
    height: 32,
    textTransform: "unset",
    fontSize: "0.75rem",
  },
  selected: {},
})(ToggleButton);

const DisabledToggle = withStyles({
  root: {
    color: "#1674f7",
    "&$selected": {
      color: "white",
      background: "#9bc2fa",
      borderColor: "#9bc2fa",
    },
    "&:hover": {
      color: "1665f7",
      borderColor: "",
      background: "white",
    },

    "&:hover$selected": {
      color: "1665f7",
      borderColor: "#9bc2fa",
      background: "#9bc2fa",
    },
    minWidth: 32,
    maxWidth: 32,
    height: 32,
    textTransform: "unset",
    fontSize: "0.75rem",
  },
  selected: {},
})(ToggleButton);

const DaysSelector = (props) => {
  const [days, setDays] = useState(props.days);
  const ButtonToggle = props.isDisabled ? DisabledToggle : StyledToggle;
  return (
    <>
      <StyledToggleButtonGroup
        aria-disabled
        size="small"
        arial-label="Days of the week"
        value={days}
        onChange={(event, value) => {
          if (!props.isDisabled) {
            setDays(value);
            props.onChange(value);
          }
        }}
      >
        {DAYS.map((day, index) => (
          <ButtonToggle key={day.key} value={day.key} aria-label={day.key}>
            {day.label}
          </ButtonToggle>
        ))}
      </StyledToggleButtonGroup>
    </>
  );
};

export default DaysSelector;
