import React, {useState, useEffect} from "react";
import EventPagination from "../../components/ViewingAllEvents/ShowcaseEventsWithRoutes";
import axios from "axios";
import Landing from "../general_pages/Landing";
import LoadingScreen from "../../components/global/LoadingScreen";

const Events = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  const BASE_URL = process.env.REACT_APP_BASE_URL;
  useEffect(()=>{
    const fetch_events = async() => {
      try {
      const token = localStorage.getItem('accessToken');
      const request = await axios.get(`${BASE_URL}/event/get-events`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
      setEvents(request.data);
      setLoading(false)
              }
          catch {
              console.log("Error fetching events...");
          }
        }
        fetch_events();
        console.log(events);
        
    },[]);
  
if (loading)
  return <LoadingScreen/>;
return (
    <>
  
    <EventPagination events={events} title={"Upcoming Events"} navigationLink={"/auth/view-event-description"}/>
    </>
  );
  
};

export default Events;