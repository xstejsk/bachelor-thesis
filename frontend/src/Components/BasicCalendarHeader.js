import React from "react";
import { Form } from "react-bootstrap";

const BasicCalendarHeader = ({ locationOptions, handleLocationChange }) => {
  return (
    <div className="basicCalendarToolbar">
      <Form.Select
        id="locationSelect"
        size="md"
        onChange={(e) => {
          console.log("handle location change");
          console.log(e.target.value);
          console.log(locationOptions);
          handleLocationChange(e.target.value);
        }}
      >
        {locationOptions.map((locationOption) => (
          <option value={locationOption.value} key={locationOption.value}>
            {locationOption.label}
          </option>
        ))}
      </Form.Select>
    </div>
  );
};

export default BasicCalendarHeader;
