import React, { useEffect, useState } from "react";
import {
  host,
  locationsEndpoint,
  eventsEndpoint,
} from "../util/EndpointConfig";
import axios from "axios";
import CustomGridLoader from "./CustomLoader";
import CustomScheduler from "./CustomScheduler";
import { useAlert } from "react-alert";

const SchedulerWrapper = () => {
  const [events, setEvents] = useState([]);
  const [eventsLoaded, setEventsLoaded] = useState(false);
  const [locations, setLocations] = useState({
    locationOptions: [],
    locationObjects: [],
  });
  const [currentLocation, setCurrentLocation] = useState(undefined);
  const alert = useAlert();

  const handleLocationChange = (locationId) => {
    // setEventsLoaded(false);
    const locationById = locations.locationObjects.find(
      (location) => location.id == locationId
    );
    setCurrentLocation(locationById);
  };

  useEffect(() => {
    if (currentLocation !== undefined) {
      reloadEvents();
    }
  }, [currentLocation]);

  useEffect(() => {
    setEventsLoaded(true);
    console.log("events----------------")
    console.log(events)
    console.log("----------------")
  }, [events]);

  const reloadLocations = () => {
    axios.get(host + locationsEndpoint).then((response) => {
      if (response.status === 200) {
        let location = response.data.at(0);
        setCurrentLocation(location);
        let locationOptions = response.data.map((location) => ({
          value: location.id,
          label: location.name,
        }));
        setLocations({
          locationObjects: response.data,
          locationOptions: locationOptions,
        });
      }
    });
  };

  const reloadEvents = () => {
    // setEventsLoaded(false);
    axios
      .get(host + eventsEndpoint + "?locationId=" + currentLocation.id)
      .then((response) => {
        if (response.status === 200) {
          setEvents(response.data);
        }
      })
      .catch((err) => {
        console.log(err);
        setEvents([]);
      });
  };

  const deleteCalendar = () => {
    axios
      .delete(host + locationsEndpoint + "/" + currentLocation.id)
      .then((response) => {
        alert.success("Kalendář a příslušné události byly smazány.");
        reloadLocations();
      })
      .catch((error) => {
        if (error.response.status === 409) {
          alert.error("Poslední kalendář nelze odstranit");
        }
        alert.error("Kalendář nelze zrušit");
      });
  };

  const initialize = () => {
    console.log("initialising");
    axios
      .get(host + locationsEndpoint)
      .then((response) => {
        if (response.status === 200) {
          let locationsData = response.data;
          let location = response.data.at(0);
          setCurrentLocation(location);
          let locationOptions = response.data.map((location) => ({
            value: location.id,
            label: location.name,
          }));
          setLocations({
            locationObjects: locationsData,
            locationOptions: locationOptions,
          });
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    initialize();
  }, []);

  return !eventsLoaded || currentLocation === undefined ? (
    <CustomGridLoader />
  ) : (
    <div className="container-fluid">
      <div id="calendar">
        <CustomScheduler
          events={events}
          currentLocationId={currentLocation.id}
          reloadLocations={reloadLocations}
          reloadEvents={reloadEvents}
          closesAt={currentLocation.closesAt}
          opensAt={currentLocation.opensAt}
          handleLocationChange={handleLocationChange}
          locationOptions={locations.locationOptions}
          deleteCalendar={deleteCalendar}
        />
      </div>
    </div>
  );
};

export default SchedulerWrapper;
