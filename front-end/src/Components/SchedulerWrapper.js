import React, { useEffect, useState } from "react";
import {
  host,
  locationsEndpoint,
  activeEventsEndpoint,
} from "../util/EndpointConfig";
import Select from "react-select";
import axios from "axios";
import CustomGridLoader from "./CustomLoader";
import CustomScheduler from "./CustomScheduler";
import { Row, Col } from "reactstrap";

const SchedulerWrapper = () => {
  const [events, setEvents] = useState({ data: [], loaded: false });
  const [locations, setLocations] = useState({
    locationOptions: [],
    locationObjects: [],
  });
  const [currentLocationId, setCurrentLocationId] = useState(0);

  const handleLocationChange = (locationId) => {
    setCurrentLocationId(locationId);
  };

  useEffect(() => {
    console.log(events.data);
  }, [events]);

  const reloadLocations = () => {
    axios.get(host + locationsEndpoint).then((response) => {
      if (response.status === 200) {
        let locationsData = response.data;
        let locationId;
        if (currentLocationId !== 0) {
          locationId = currentLocationId;
        } else {
          locationId = response.data.at(0).id;
          setCurrentLocationId(locationId);
        }
        let locationOptions = response.data.map((location) => ({
          value: location.id,
          label: location.name,
        }));
        setLocations({
          locationObjects: locationsData,
          locationOptions: locationOptions,
        });
      }
    });
  };

  const reloadEvents = () => {
    axios
      .get(host + activeEventsEndpoint, {
        timeout: 10000,
      })
      .then((response) => {
        if (response.status === 200) {
          setEvents({ data: response.data, loaded: true });
        }
      })
      .catch((err) => {
        console.log(err);
        setEvents({ data: [], loaded: true });
      });
  };

  const initialize = () => {
    axios
      .get(host + locationsEndpoint)
      .then((response) => {
        if (response.status === 200) {
          let locationsData = response.data;
          let locationId;
          if (currentLocationId !== 0) {
            locationId = currentLocationId;
          } else {
            locationId = response.data.at(0).id;
            setCurrentLocationId(locationId);
          }
          let locationOptions = response.data.map((location) => ({
            value: location.id,
            label: location.name,
          }));
          if (locationsData.size === 0) {
            setEvents({ data: [], loaded: true });
            return;
          }
          setLocations({
            locationObjects: locationsData,
            locationOptions: locationOptions,
          });
          axios
            .get(host + activeEventsEndpoint, {
              timeout: 10000,
            })
            .then((response) => {
              if (response.status === 200) {
                setEvents({ data: response.data, loaded: true });
              }
            })
            .catch((err) => {
              console.log(err);
              // handle timeout
            });
        }
      })
      .catch((err) => {
        console.log(err);
        setEvents([]);
        // handle timeout
      });
  };

  useEffect(() => {
    initialize();
  }, []);

  if (!events.loaded) {
    return <CustomGridLoader />;
  } else {
    return (
      <div className="container-fluid">
        {/* <div className="row"> */}
        {/* <div className="col-auto min-vh-100 bg-light"> */}
        <div id="locations">
          {/* <Row>
            <Col> */}
          <Select
            style={{ float: "left" }}
            defaultValue={locations.locationOptions[0]}
            options={locations.locationOptions}
            onChange={(element) => handleLocationChange(element.value)}
          />
          {/* </Col>
          </Row> */}
        </div>
        <div id="calendar">
          {/* <Row>
            <Col> */}
          <CustomScheduler
            allEvents={events.data}
            currentLocationId={currentLocationId}
            reloadLocations={reloadLocations}
            reloadEvents={reloadEvents}
          />
          {/* </Col>
          </Row> */}
        </div>

        {/* </div> */}
        {/* <div className="col "> */}
      </div>
      // </div>
      // </div>

      // <div className="">
      //   <div id="locations">
      //     <Row>
      //       <Select
      //         style={{ float: "left" }}
      //         defaultValue={locations.locationOptions[0]}
      //         options={locations.locationOptions}
      //         onChange={(element) => handleLocationChange(element.value)}
      //       />
      //     </Row>
      //   </div>

      //   <div id="calendar"></div>
      //   <Row>
      //     <CustomScheduler
      // allEvents={events.data}
      // currentLocationId={currentLocationId}
      // reloadLocations={reloadLocations}
      // reloadEvents={reloadEvents}
      //     />
      //   </Row>
      // </div>
    );
  }
};

export default SchedulerWrapper;
