import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Landing from '../general_pages/Landing';
import {useLocation } from 'react-router-dom';
import LoadingScreen from '../../components/global/LoadingScreen';
import { useNavigate } from 'react-router-dom';
import ViewCreatedEvents from './ViewCreatedEvents';

const Dashboard = () => {
    // Store whether user is authorized to view page or not
    const[isAuthorized, setIsAuthorized] = useState(null);
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    
    //Store all variables needed to render dashboard page
    const[eventsCreated, setEventsCreated] = useState(0);
    const[totalAttendees, setTotalAttendees] = useState(0);
    const[totalRevenue, setTotalRevenue] = useState(0);
    const[eventsHosted, setEventsHosted] = useState(0);
    const[bestHostedEvent, setBestHostedEvent] = useState("None");
    const[bestCurrentEvent, setBestCurrentEvent] = useState("None");
    const[spending,setSpending] = useState(0);
    const[eventsAttended,setEventsAttended] = useState(0);
    const[registered,setEventsRegistered] = useState(0);
    const[userloggedin,setLoggedInUser] = useState("");

    const navigate = useNavigate();

    const handleRegisteredEventsClick = () => {
        navigate("/auth/view-registered-events")
    }

    const handleViewCreatedEventsClick = () => {
        navigate("/auth/view-created-events");
    }

    const handleAddEventsClick = () => {
        navigate("/auth/create-event");
    }


    useEffect(() => {
        const fetch_user_statistics = async() => {
            try {
                //Using JWT token retrieve analytics
                const token = localStorage.getItem("accessToken");
                console.log(token);
                 // Retrieve statistics for registered/attended events and hosted/hosting events
                const request_customer_data = await axios.get(`${BASE_URL}/user/get_customer_statistics`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const request_host_data = await axios.get(`${BASE_URL}/user/get_host_statistics`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                let host_data = request_host_data.data;
                let customer_data = request_customer_data.data;
                console.log(host_data);
                console.log(customer_data);
                setTotalRevenue(host_data["Revenue"]);
                setBestCurrentEvent(host_data["Best-Current-Event"]);
                setBestHostedEvent(host_data["Best-Hosted-Event"]);
                setEventsCreated(host_data["Created"]);
                setTotalAttendees(host_data["Attendees"]);
                setEventsHosted(host_data["Hosted"]);
                setEventsAttended(customer_data["Attended"]);
                setSpending(customer_data["Spending"]);
                setEventsRegistered(customer_data["Registered"]);
                setLoggedInUser(host_data["User"]);
                setIsAuthorized(true);
            } catch {
                console.log("Error fetching user statistics...");
                setIsAuthorized(false);
            }
        }
        fetch_user_statistics();
    },[])
    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        navigate('/');
        setTimeout(() => {
            window.alert('Please ensure you are logged in.');
        }, 500);
    };

    // If still processing return loading screen
    if(isAuthorized === null) {
        return <LoadingScreen/>
    }
    // If not valid return user to landing page
    else if(isAuthorized === false) {
        handleLogout();
    }
    return (
        <>
        <div className='flex flex-col h-screen w-[100%]'>
            {/* Create the header for the logged in user */}
            <div className='flex text-5xl justify-center items-center h-[10%] w-[100%]'>
                <div className='flex items-center'>
                    <b>Welcome Back {userloggedin}!</b>
                </div>
            </div>
            {/* Load all the user statistics */}
            <div className='flex flex-col w-[100%} h-[70%] mt-[1%] '>
                <div className='flex justify-center h-[33%] gap-20'>
                    <div className='flex flex-col overflow-hidden items-center bg-blue-100 w-[20%] shadow-2xl'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Events Registered:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            {registered}
                        </div>
                    </div>
                    <div className='flex flex-col overflow-hidden items-center bg-blue-100 w-[20%] shadow-2xl'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Events Attended:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            {eventsAttended}
                        </div>
                    </div>
                    <div className='flex flex-col overflow-hidden items-center bg-blue-100 w-[20%] shadow-2xl truncate'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Total Spending:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            ${spending}
                        </div>
                    </div>
                </div>
                <div className='flex justify-center h-[33%] gap-20 mt-[2%]'>
                     <div className='flex flex-col overflow-hidden items-center bg-blue-200 w-[20%] shadow-2xl'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Events Created:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            {eventsCreated}
                        </div>
                    </div>
                    <div className='flex flex-col overflow-hidden items-center bg-blue-200 w-[20%] shadow-2xl'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Total Attendees:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            {totalAttendees}
                        </div>
                    </div>
                    <div className='flex flex-col overflow-hidden items-center bg-blue-200 w-[20%] shadow-2xl truncate'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Total Revenue:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            ${totalRevenue}
                        </div>
                    </div>
                </div>
                <div className='flex justify-center h-[33%] gap-20 mt-[2%]'>
                    <div className='flex flex-col overflow-hidden items-center bg-blue-300 w-[20%] shadow-2xl'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Events Hosted:</b>
                        </div>
                        <div className='flex items-center text-7xl h-[80%]'>
                            {eventsHosted}
                        </div>
                    </div>
                    <div className='flex flex-col items-center bg-blue-300 w-[20%] shadow-2xl truncate'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Best Hosted Event:</b>
                        </div>
                        <div className='flex items-center text-3xl h-[80%]'>
                            {bestHostedEvent}
                        </div>
                    </div>
                    <div className='flex flex-col items-center bg-blue-300 w-[20%] shadow-2xl truncate'>
                        <div className='flex text-2xl h-[10%] mt-[1%]'>
                            <b>Best Current Event:</b>
                        </div>
                        <div className='flex items-center text-3xl h-[80%]'>
                            {bestCurrentEvent}
                        </div>
                    </div>
                </div>
            </div>
            {/* Give user options of redirection */}
            <div className='flex h-[10%] gap-20 justify-center items-center'>
                <div onClick={handleRegisteredEventsClick} className='flex w-[20%] bg-blue-400 h-[50%] justify-center items-center rounded border border-black'>
                    <b>View Registered Events</b>
                </div>
                <div onClick={handleAddEventsClick} className='flex w-[20%] bg-blue-400 h-[50%] justify-center items-center rounded border border-black'>
                    <b>Add Event</b>
                </div>
                <div onClick={handleViewCreatedEventsClick} className='flex w-[20%] bg-blue-400 h-[50%] justify-center items-center rounded border border-black'>
                    <b>View Your Events</b>
                </div>
            </div>

        </div>
        </>
    )

}

export default Dashboard;