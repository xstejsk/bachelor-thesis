import React, { useEffect, useState } from "react";
import {
  host,
  locationsEndpoint,
  activeEventsEndpoint,
} from "../util/EndpointConfig";
import axios from "axios";
import CustomGridLoader from "./CustomLoader";
import CustomScheduler from "./CustomScheduler";

const SchedulerWrapper = () => {
  const [events, setEvents] = useState({ data: [], loaded: false });
  const [locations, setLocations] = useState({
    locationOptions: [],
    locationObjects: [],
  });
  const [currentLocationId, setCurrentLocationId] = useState(0);

  const handleLocationChange = (locationId) => {
    console.log(locationId);
    setCurrentLocationId(locationId);
  };

  useEffect(() => {
    console.log(events.data);
  }, [events]);

  useEffect(() => {
    console.log("current location id");
    console.log(currentLocationId);
    reloadEvents();
  }, [currentLocationId]);

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
    console.log("inside reload events");
    console.log(currentLocationId);
    axios
      .get(host + activeEventsEndpoint + "?locationId=" + currentLocationId, {
        timeout: 10000,
      })
      .then((response) => {
        if (response.status === 200) {
          setEvents({ data: response.data, loaded: true });
          console.log("events were set");
          console.log(response.data);
        }
        console.log(response.status);
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
          if (currentLocationId != 0) {
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
        }
      })
      .catch((err) => {
        console.log(err);
        setEvents([]);
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
        <div id="calendar">
          <CustomScheduler
            events={events.data}
            currentLocationId={currentLocationId}
            reloadLocations={reloadLocations}
            reloadEvents={reloadEvents}
            handleLocationChange={handleLocationChange}
            locationOptions={locations.locationOptions}
          />
        </div>
      </div>
    );
  }
};

export default SchedulerWrapper;
