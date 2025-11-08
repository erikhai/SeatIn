import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {useLocation } from 'react-router-dom';
import LoadingScreen from '../../components/global/LoadingScreen';
import Landing from '../general_pages/Landing';
import { useNavigate } from 'react-router-dom';

const EventPreview = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const[isLoggedIn, setIsLoggedIn] = useState(null);
    const location = useLocation();
    const query = new URLSearchParams(location.search);
    //Fetch the eventId so we can load it
    const eventId = query.get('eventId'); 

    // Store whether event is completed or not
    const[isRunning, setIsRunning] = useState(false);

    //Store all information needed to load page
    const [eventName, setEventName] = useState("None");
    const [description,setDescription] = useState("No Description");
    const [venueLocation,setVenueLocation] = useState("No Venue");
    const [startTime,setStartTime] = useState("No Time");
    const [duration,setDuration] = useState(0);
    const [tiers,setTiers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetch_event_details = async(event_id) => {
            try {
                //Using JWT token retrieve analytics
                const token = localStorage.getItem("accessToken");
                console.log(token);

                //First fetch all details of the event
                const request_event_details = await axios.get(`${BASE_URL}/event/get-event-details/` + event_id, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                //Create another request to fetch all tiers in event given id
                const request_event_tiers = await axios.get(`${BASE_URL}/event/get-event-tiers/` + event_id, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                // Check if event is still running
                const event_status = await axios.get(`${BASE_URL}/event/get-event-status/` + event_id, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                //Update status of event
                setIsRunning(event_status.data);
                //Now store all the data given
                console.log(request_event_details.data);
                setDescription(request_event_details.data.description);
                setVenueLocation(request_event_details.data.location);
                setDuration(request_event_details.data.duration);
                setStartTime(request_event_details.data.start_time);
                setEventName(request_event_details.data.ename);
                setTiers(request_event_tiers.data);
                setIsLoggedIn(true);
            }
            catch {
                console.log("Error fetching Event Description...");
                setIsLoggedIn(false);
            }
        }
        fetch_event_details(eventId);
    },[])

    // Create a component for tier and price since we want it to be dynamically rendered
    const TierAndPrice = ({tier_name, tier_price}) => {
        return (
            <>
            {/* When laying out component just want name on left and price on right */}
            <div className='flex justify-between w-[100%]'>
                <div className='flex ml-[5%] text-2xl'>{tier_name}:</div>
                <div className='flex mr-[5%] text-2xl'>${tier_price}</div>
            </div>
            </>
        )
    }

    const handleRedirect = (event) => {
        console.log("Event is is:" + eventId);
        navigate(`/book?id=${eventId}`)
    }

     // If still processing return loading screen
    if(isLoggedIn === null) {
        return <LoadingScreen/>
    }
    // If not valud return user to landing page
    else if(isLoggedIn === false) {
        return <Landing/>
    }
    return (
        <>
        <div className='min-h-screen'>
        <div className='flex justify-center items-center h-[10%] w-[100%]'>
            <div className='flex justify-center text-6xl'>
                <b> Event Description</b>
            </div>
        </div>

        <div className='flex justify-center space-x-20 w-[100%] mt-[1%]'>
            <div className='flex flex-col shadow-2xl w-[40%]'>
                <div className='flex justify-center mt-[5%]'>
                    <b className='text-4xl'>{eventName}</b>
                </div>
                <div className='flex flex-col mt-[5%] ml-[5%]'>
                    <div className='h-full flex flex-col overflow-hidden h-[10%]'> 
                        <b className='text-2xl'>Event Description:</b>
                        <div className='text-xl'>{description}</div>
                    </div>
                    <div className='flex flex-col'>
                        <b className='text-2xl'>Venue Located:</b>
                        <div className='text-xl'>{venueLocation}</div>
                    </div>
                    <div className='flex flex-col'>
                        <b className='text-2xl'>Start Time:</b>
                        <div className='text-xl'>{startTime}</div>
                    </div>
                    <div className='flex flex-col mb-[15%]'>
                        <b className='text-2xl'>Duration:</b>
                        <div className='text-xl'>{duration} Minutes</div>
                    </div>
                </div>
            </div>

            <div className='flex flex-col space-y-10 items-center w-[40%]'>
                <div className='flex h-[65%] justify-center shadow-2xl w-[100%]'>
                    <div className='flex flex-col mt-[5%] w-[100%]'>
                        <b className='flex justify-center text-4xl'>Seat Pricing</b>
                        <div className='flex flex-col w-[100%] h-[70%] justify-center'>
                            {/* Dynamically render each tier for display */}
                            {tiers.map((tier) => (<TierAndPrice tier_name={tier.name} tier_price={tier.price}/>))}
                        </div>
                    </div>
                </div>
                <div className='flex flex-col h-[35%] justify-center shadow-2xl w-[100%]'>
                    <div className='flex flex-col justify-center items-center mt-[2%] h-[20%] w-[100%]'>
                        <b className='text-4xl'>Accessibility</b>
                    </div>
                    <div className='flex items-center space-x-[20%] justify-center mt-[5%] h-[80%] w-[100%] mb-[5%]'>
                        <img className = 'max-w-20 max-h-10' src='/disabled.png' alt="test"></img>
                        <img className = 'max-w-20 max-h-10' src='/closed-caption-logo.png' alt="test"></img>
                        <img className = 'max-w-20 max-h-10' src='/usability.png' alt="test"></img>
                    </div>
                </div>
            </div>
        </div>
        {/*Only if event has not ended give option to book tickets */}
        {isRunning === true &&  
        <div className='flex justify-center mt-[3%] mb-[5%]'>
            <button className='rounded-xl bg-blue-500 px-10 py-4 h-[100%] w-[15%] text-white' onClick={handleRedirect}>
                Book Seats
            </button>
        </div>
        }

         {/* If event ended do not allow to book tickets */}
        {isRunning === false &&  
        <div className='flex justify-center mt-[3%]'>
            <button className='rounded-xl bg-gray-500 px-10 py-4 w-[15%] text-white'>
                Event Ended
            </button>
        </div>}
        </div>
        </>
    )
}

export default EventPreview;