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

  const addEvents = (newEvents) => {
    //  setEvents({ ...events, loaded: false });
    let eventsData = events.data.concat(newEvents);
    setEvents({ data: eventsData, loaded: true });
  };

  const addLocation = (location) => {
    let locationObjects = locations.locationObjects;
    locationObjects.push(location);
    setLocations({ ...locations, locationObjects: locationObjects });
  };

  const removeEvent = (id) => {
    console.log(events);
    //    setEvents({ ...events, loaded: false });
    let eventsData = events.data.filter((event) => event.id != id);
    setEvents({ data: eventsData, loaded: true });
  };

  const removeEventGroup = (id) => {
    let eventsData = events.data.filter(
      (event) => event.recurrenceGroup?.id != id
    );
    setEvents({ ...events, data: eventsData });
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
        <div className="row">
          {/* <div className="col-auto min-vh-100 bg-light"> */}

          <Select
            style={{ float: "left" }}
            defaultValue={locations.locationOptions[0]}
            options={locations.locationOptions}
            onChange={(element) => handleLocationChange(element.value)}
          />
          {/* </div> */}
          <div className="col ">
            <CustomScheduler
              allEvents={events.data}
              currentLocationId={currentLocationId}
              addEvents={addEvents}
              removeEvent={removeEvent}
              removeEventGroup={removeEventGroup}
              addLocation={addLocation}
            />
          </div>
        </div>
      </div>
    );
  }
};

export default SchedulerWrapper;
