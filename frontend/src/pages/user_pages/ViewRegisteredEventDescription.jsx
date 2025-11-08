import LoadingScreen from '../../components/global/LoadingScreen'
import React, { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import useAuth from '../../components/auth/UseAuth'
import axios from 'axios'

const ViewRegisteredEventDescription = () => {
    const isLoggedIn = useAuth()
    const [isLoading, setIsLoading] = useState(true)
    const [eventData, setEventData] = useState(false)
    const navigate = useNavigate()
    const location = useLocation()
    const query = new URLSearchParams(location.search)
    const eventId = query.get('eventId')
    const [organiser, setOrganiser] = useState("")
    const [yourSeatsData, setYourSeatsData] = useState([])
    const [allSeatsData, setAllSeatsData] = useState([])

    const BASE_URL = process.env.REACT_APP_BASE_URL
    const [isUpcoming, setIsUpcoming] = useState(null)
    const [totalCost, setTotalCost] = useState(0)
    const handleLogout = () => {
        localStorage.removeItem('accessToken');
        navigate('/');
        setTimeout(() => {
            window.alert('Please ensure you are logged in.');
        }, 500);
    };
    useEffect(() => {
        if (isLoggedIn === null) return

        if (isLoggedIn === false) {
            handleLogout()
            return
        } else if (!eventId) {
            window.alert("Event was not found")
            navigate("/auth/view-registered-events")
        } else {

            const fetchEventDetails = async () => {
                try {
                    const token = localStorage.getItem('accessToken')
                    console.log("Token being sent:", token)
                    let response = await axios.get(`${BASE_URL}/event/valid-registered-event`, {
                        params: { eventId },
                        headers: { Authorization: `Bearer ${token}` }
                    })


                    setEventData(response.data.validUserRegistered)
                    console.log("Fetched event data:", response.data.validUserRegistered)
                    console.log(eventData.start, Date.now())
                    setIsUpcoming(new Date(eventData.start) > Date.now())
                    console.log("Is the event upcoming?", isUpcoming)
                    response = await axios.get(`${BASE_URL}/event/get-organiser`, {
                        params: { eventId },
                        headers: { Authorization: `Bearer ${token}` }
                    })
                    setOrganiser(response.data.organiser)
                    response = await axios.get(`${BASE_URL}/seat/get-all-seats-for-event`, {
                        params: { eventId },
                        headers: { Authorization: `Bearer ${token}` }
                    })
                    setYourSeatsData(response.data.seats || [])
                    const total = (response.data.seats || []).reduce((sum, seat) => sum + seat.tierPrice, 0)
                    setTotalCost(total)
                    // response = await axios.get(`${BASE_URL}/seat/get-all-seats `, {
                    //     params: { eventId },
                    //     headers: { Authorization: `Bearer ${token}` }
                    // })
                    
                    // setAllSeatsData(response.data.seats || [])

                } catch (error) {
                    console.error("Error fetching event:", error)
                    window.alert("Failed to fetch event details")
                    navigate("/auth/view-registered-events")
                } finally {
                    setIsLoading(false)
                }
            }

            fetchEventDetails()
        }
    }, [isLoggedIn, totalCost])

    const calculateEndDate = (startDate, durationMinutes) => {
        const start = new Date(startDate)
        const end = new Date(start.getTime() + durationMinutes * 60000)
        return end.toLocaleString()
    }

    const handleCancelRegistration = async () => {
        if (!window.confirm("Are you sure you want to cancel your registration for this event?")) {
            return
        }
        try {
            const token = localStorage.getItem('accessToken')
            const response = await axios.patch(`${BASE_URL}/seat/remove-registered-seats`,
                { eventId },
                {
                    headers: { Authorization: `Bearer ${token}` }
                }
            )
            if (response.data.Success === true) {
                try {
                   
                    await axios.post(`${BASE_URL}/email/send-unregister-event`,
                        {
                            eventTitle: eventData.ename,
                            startDate: eventData.start,
                            cost: totalCost,
                            location: eventData.location,
                            organiser: organiser
                        },
                        {
                            headers: { Authorization: `Bearer ${token}` }
                        }
                    )

                    console.log("Unregistration email sent successfully")
                } catch (error) {

                }

                window.alert("Your registration has been successfully cancelled.")
                navigate("/auth/view-registered-events")
            } else {
                window.alert("Failed to cancel registration. Please try again later.")
            }

        } catch (error) {
            console.error("Error cancelling registration:", error)
            window.alert("Failed to cancel registration. Please try again later.")
        }
    }

    

    if (isLoading || isLoggedIn === null) {
        return <LoadingScreen />
    }

    return (
        <div className="min-h-screen bg-gray-50">



            {eventData ? (
                <div className="max-w-7xl mx-auto px-4 py-8">
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">

                        <div className="bg-white rounded-lg shadow-lg p-6">
                            <h2 className="text-2xl font-bold text-blue-600 mb-6">{eventData.ename}</h2>

                            <div className="space-y-4">
                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Description</h3>
                                    <p className="text-gray-800 mt-1">{eventData.description}</p>
                                </div>

                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Start Date</h3>
                                    <p className="text-gray-800 mt-1">{new Date(eventData.start).toLocaleString()}</p>
                                </div>

                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Duration</h3>
                                    <p className="text-gray-800 mt-1">{eventData.duration} minutes</p>
                                </div>

                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600 uppercase tracking-wide">End Date</h3>
                                    <p className="text-gray-800 mt-1">{calculateEndDate(eventData.start, eventData.duration)}</p>
                                </div>

                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Location</h3>
                                    <p className="text-gray-800 mt-1">{eventData.location}</p>
                                </div>

                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600 uppercase tracking-wide">Organiser</h3>
                                    <p className="text-gray-800 mt-1">{organiser}</p>
                                </div>
                            </div>
                        </div>

                        <div className="bg-white rounded-lg shadow-lg p-6">
                           
                            <h2 className="text-xl font-semibold text-gray-700 mb-4">Booked Seats</h2>
                            {yourSeatsData.length === 0 ? (
                                <p className="text-gray-500">No seats booked yet.</p>
                            ) : (
                                <div>

                                    <ul className="space-y-2">
                                        {yourSeatsData.map((s, index) => (
                                            <li key={index} className="flex justify-between  px-3 py-1 rounded">
                                                <span>Seat {s.row + 1}-{s.column + 1}</span>
                                                <span>{s.tierName}</span>
                                                <span>${s.tierPrice}</span>
                                            </li>
                                        ))}
                                    </ul>
                                    <hr />
                                    <ul className="space-y-2">
                                        <li className="flex justify-between  px-3 py-1 rounded">
                                            <span className="text-lg font-semibold text-gray-800">Total Cost:</span>
                                            <span></span>
                                            <span>${totalCost}</span>
                                        </li>
                                    </ul>

                                </div>
                            )}
                            {isUpcoming !== false && (
                                <div className="text-center">
                                    <button className="bg-red-600 hover:bg-red-700 text-white font-medium py-3 px-6 rounded-lg transition-colors duration-200" onClick={handleCancelRegistration}>
                                        Cancel Registration
                                    </button>
                                </div>
                            )}
                        
                        </div>
                    </div>
                </div>
            ) : (
                <div className="max-w-7xl mx-auto px-4 py-8">
                    <div className="bg-white rounded-lg shadow-lg p-6 text-center">
                        <p className="text-gray-600">No event data found.</p>
                    </div>
                </div>
            )}
           

        </div>
    )
}

export default ViewRegisteredEventDescription
