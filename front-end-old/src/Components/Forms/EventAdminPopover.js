import React from "react";
import {
  UncontrolledPopover,
  Button,
  Popover,
  PopoverHeader,
  PopoverBody,
} from "reactstrap";

const EventAdminPopover = ({ target }) => {
  if (!target) {
    console.log("no event");
    return null;
  }
  return (
    <UncontrolledPopover placement="auto" target={target} trigger="legacy">
      <PopoverHeader>Popover Title</PopoverHeader>
      <PopoverBody>
        Sed posuere consectetur est at lobortis. Aenean eu leo quam.
        Pellentesque ornare sem lacinia quam venenatis vestibulum.
      </PopoverBody>
    </UncontrolledPopover>
  );
};

export default EventAdminPopover;
