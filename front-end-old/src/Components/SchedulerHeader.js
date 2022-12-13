import React, { useEffect, useState, useRef, useContext } from "react";
import { host, locationsEndpoint } from "../util/EndpointConfig";
import Select from "react-select";
import axios from "axios";
import NewEventForm from "./Forms/NewEventForm";
import { Context } from "../util/GlobalState";
import { Row, Col, Button } from "reactstrap";
import CustomScheduler from "./CustomScheduler";

const SchedulerHeader = () => {
  const [locations, setLocations] = useState();
  const [currentLocation, setCurrentLocation] = useState(null);
  const [globalState, setGlobalState] = useContext(Context);
  const calendarRef = useRef(null);
  const [showAddEventForm, setShowAddEventForm] = useState(false);

  let locationOptions = [];
  axios
    .get(host + locationsEndpoint)
    .then((response) => {
      if (response.status === 200) {
        console.log(response.data);
        setLocations(response.data);
        locationOptions = response.data.map((location) => ({
          value: location,
          label: location.name,
        }));
        console.log(locationOptions);
        if (response.data.size !== 0) {
          if (currentLocation === null) {
            console.log("setting");
            setCurrentLocation(response.data.at(0));
          }
        }
        console.log(response.data.at(0));
        console.log(currentLocation);
      }
    })
    .catch((err) => console.log(err));

  return (
    <div
      style={{
        //justifyContent: "center",
        //alignItems: "center",
        //display: "flex",
        paddingTop: 3,
        paddingLeft: 10,
        paddingRight: 10,
        //height: "100%",
        // background: "#c93060",
      }}
    >
      <Row style={{ marginBottom: 20 }}>
        <Col
          sm={{ size: 3 }}
          md={{ size: 3 }}
          style={{
            paddingLeft: 15,
          }}
        >
          <Select
            style={{ float: "left" }}
            defaultValue={currentLocation}
            options={locationOptions}
            onChange={(element) => {
              console.log(element);
              //setCurrentLocation(element.)}
            }}
          />
        </Col>
        <Col
          sm={{ size: 3, offset: 6 }}
          md={{ size: 3, offset: 6 }}
          style={{
            paddingRight: 15,
          }}
        >
          <Button
            style={{ float: "right" }}
            color="primary"
            //onClick={() => setShowAddEventForm(true)}
          >
            Nová událost
          </Button>
        </Col>
      </Row>
      {currentLocation && (
        <CustomScheduler location={currentLocation} ref={calendarRef} />
      )}
      {calendarRef && ( // dunno if will work
        <NewEventForm
          isOpen={showAddEventForm}
          setIsOpen={setShowAddEventForm}
          calendarRef={calendarRef}
          locationId={currentLocation?.id}
        />
      )}
    </div>
  );
};

export default SchedulerHeader;
