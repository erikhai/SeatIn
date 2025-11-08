import React, { useEffect, useState } from 'react';
import axios from 'axios';

import AppPagination from '../../components/ViewingAllEvents/ShowcaseEventsWithRoutes';
import Landing from '../general_pages/Landing';
import LoadingScreen from '../../components/global/LoadingScreen';

const ViewCreatedEvents = () => {
    const[eventsHosting, setEventsHosting] = useState([]);
    const[isLoggedIn, setIsLoggedIn] = useState(null);
    const[isLoading, setIsLoading] = useState(true);

    const BASE_URL = process.env.REACT_APP_BASE_URL;
    

    useEffect (() => {
        // Fetch all events the user is hosting
        const fetch_created_events = async() => {
            try {
                // Using JWT token retrieve analytics
                 const token = localStorage.getItem("accessToken");
                 //console.log(token);
                 const request = await axios.get(`${BASE_URL}/event/hosting_events`, {
                     headers: {
                         'Authorization': `Bearer ${token}`
                     }
                });
                // Set the events user is hosting 
                const event = request.data;
                setEventsHosting(event);
                setIsLoggedIn(true);
                setIsLoading(false);
            } catch {
                console.log("Error fetching events hosted...");
                setIsLoading(false);
                setIsLoggedIn(false);
            }
        }
        fetch_created_events();
    },[isLoggedIn, BASE_URL]);
   
    //If user is not logged in trying to access web-page redirect to landing page
    if(isLoggedIn === null || isLoading === true) {
        return (
            <>
            <LoadingScreen/>
            </>
        )
    }
    
    return (
        <>
        {/* Link the page to the analytics page */}
        <AppPagination events={eventsHosting} title={"Hosting Events"} navigationLink={"/auth/analytics"} />
        </>
    )
}

export default ViewCreatedEvents;