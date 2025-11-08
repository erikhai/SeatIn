import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuth from '../../components/auth/UseAuth';
import LoadingScreen from '../../components/global/LoadingScreen';
import axios from 'axios';
import AppPagination from '../../components/ViewingAllEvents/ShowcaseEventsWithRoutes';


const ViewRegisteredEvents = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
   
    const [isLoading, setIsLoading] = useState(true);
    const [registeredEvents, setRegisteredEvents] = useState([]);
    const isLoggedIn = useAuth();
    const navigate = useNavigate();


   
    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        navigate('/');
        setTimeout(() => {
            window.alert('Please ensure you are logged in.');
        }, 500);
    };

    useEffect(() => {
        if (isLoggedIn === null) return;

        if (isLoggedIn === false) {
            handleLogout();
            return;
        }

        if (isLoggedIn === true) {
            const token = localStorage.getItem('accessToken');
            axios.get(`${BASE_URL}/event/get-registered-events`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            .then(response => {
                setRegisteredEvents(response.data.registeredEvent || []);
                setIsLoading(false);
            })
            .catch(() => setIsLoading(false));
        }
    }, [isLoggedIn, BASE_URL]);

    if (isLoading || isLoggedIn === null) return <LoadingScreen />;

    return (
        <>
            <AppPagination events={registeredEvents} title={"Registered Events"} navigationLink={"/auth/view-registered-events/description"} />

        </>
    );
};

export default ViewRegisteredEvents;