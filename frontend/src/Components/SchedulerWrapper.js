import React, { useEffect, useState } from "react";
import {
  host,
  locationsEndpoint,
  activeEventsEndpoint,
  deleteCalendarEndpoint,
} from "../util/EndpointConfig";
import axios from "axios";
import CustomGridLoader from "./CustomLoader";
import CustomScheduler from "./CustomScheduler";
import { useAlert } from "react-alert";

const SchedulerWrapper = () => {
  const [events, setEvents] = useState({ data: [], loaded: false });
  const [locations, setLocations] = useState({
    locationOptions: [],
    locationObjects: [],
  });
  const [currentLocationId, setCurrentLocationId] = useState(undefined);
  const [locationOpensAt, setLocationOpensAt] = useState(undefined);
  const [locationClosesAt, setLocationClosesAt] = useState(undefined);
  const alert = useAlert();

  const handleLocationChange = (locationId) => {
    setCurrentLocationId(locationId);
    // console.log(locations.locationObjects.find((location) => location.id == 1));
    const locationById = locations.locationObjects.find(
      (location) => location.id == locationId
    );
    // console.log(locationById);
    setLocationOpensAt(locationById.opensAt);
    setLocationClosesAt(locationById.closesAt);
  };

  useEffect(() => {
    if (currentLocationId !== undefined) {
      reloadEvents();
    }
  }, [currentLocationId]);

  const reloadLocations = () => {
    axios.get(host + locationsEndpoint).then((response) => {
      if (response.status === 200) {
        let locationsData = response.data;
        let location;
        if (currentLocationId !== undefined) {
          location = currentLocationId;
        } else {
          location = response.data.at(0);
          setCurrentLocationId(location.id);
          setLocationOpensAt(location.opensAt);
          setLocationClosesAt(location.closesAt);
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
      .get(host + activeEventsEndpoint + "?locationId=" + currentLocationId, {
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

  const deleteCalendar = () => {
    axios
      .delete(host + deleteCalendarEndpoint + currentLocationId)
      .then((response) => {
        alert.success("Kalendář a příslušné událisti byly smazány.");
        setCurrentLocationId(undefined);
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
    axios
      .get(host + locationsEndpoint)
      .then((response) => {
        if (response.status === 200) {
          let locationsData = response.data;
          let location;
          if (currentLocationId !== undefined) {
            location = currentLocationId;
          } else {
            location = response.data.at(0);
            setCurrentLocationId(location.id);
            setLocationOpensAt(location.opensAt);
            setLocationClosesAt(location.closesAt);
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

  if (
    !events.loaded ||
    locationOpensAt === undefined ||
    locationClosesAt === undefined
  ) {
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
            closesAt={locationClosesAt}
            opensAt={locationOpensAt}
            handleLocationChange={handleLocationChange}
            locationOptions={locations.locationOptions}
            deleteCalendar={deleteCalendar}
          />
        </div>
      </div>
    );
  }
};

export default SchedulerWrapper;
