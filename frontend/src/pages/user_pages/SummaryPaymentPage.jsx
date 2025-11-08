import React, { use, useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import useAuth from '../../components/auth/UseAuth'
import LoadingScreen from '../../components/global/LoadingScreen'
import axios from 'axios'
import { addMinutes } from 'date-fns';

const SummaryPayment = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL
    const location = useLocation();
    const [isLoading, setIsLoading] = useState(true)
    const isLoggedIn = useAuth()
    const navigate = useNavigate()
    const { selectedSeats, totalPrice, eventId } = location.state || {};
    const [event, setEvent] = useState(null)
    const [startDate, setStartDate] = useState(null)
    const [endDate, setEndDate] = useState(null)



  

    const goBack = () => {
        navigate(-1)
    }
    const goNext = async() => {
        setIsLoading(true)
        console.log("GOing to next")
        try{
            const token = localStorage.getItem("accessToken");
            console.log(token)
            await Promise.all(
                selectedSeats.map(seat => 
                    axios.post(`${BASE_URL}/seat/book-seat`, null, {
                        params: { seatId: String(seat.id) },
                        headers: { 'Authorization': `Bearer ${token}` },
                    })
                )
            );
            

            const text = `Event: ${event.title}\nLocation: ${event.location}\nStart Time: ${startDate.toLocaleString('en-AU')}\nEnd Time: ${addMinutes(startDate, event.duration).toLocaleString('en-AU')}\nSeats: ${selectedSeats.map(seat => `(${seat.row + 1},${seat.column + 1})`).join(', ')}\nTotal Paid: $${totalPrice}`;
  
            const response = await axios.post(
                `${BASE_URL}/ticket/code-generation`,
                { text },
                {
                    headers: { Authorization: `Bearer ${token}` },
                    responseType: 'blob'
                }
            );
            const imageBlob = response.data;

            const base64data = await new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onloadend = () => resolve(reader.result.split(',')[1]);
                reader.onerror = reject;
                reader.readAsDataURL(imageBlob);
            });
            await axios.post(
                `${BASE_URL}/email/send-qr`,
                { qrBase64: base64data },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            console.log('Email has been sent successfully');
            
            navigate("/auth/ticket", { state: {  event, selectedSeats, totalPrice } })


        } catch (error) {
            console.error("Error booking seats or generating QR code:", error);
            setIsLoading(false)

        }

        
    }

    const handleLogout = () => {
        localStorage.removeItem('accessToken')
        navigate('/')
        setTimeout(() => {
            window.alert('Please ensure you are logged in.')
        }, 500)
    }

    useEffect(() => {
        const checkAuthAndFetch = async () => {
            if (isLoggedIn === null) return;

            if (isLoggedIn === false) {
                handleLogout();
                return;
            }

            if (isLoggedIn === true) {
          

                try {
                    const token = localStorage.getItem("accessToken");
                    
                    console.log("location.state summary: ", location.state)
                    const request = await axios.get(`${BASE_URL}/event/get-event-details/${eventId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                    });

           //         console.log("Event", request.data);
                    setEvent(request.data)
                    setStartDate(new Date(request.data.start))
              
                    setIsLoading(false);
            
                   // console.log(seatId)
                } catch (err) {
                    console.error("Error fetching Event Description...", err);

                }
            }
        };

        checkAuthAndFetch();
    }, [isLoggedIn]);


    if (isLoading || isLoggedIn === null) {
        return (
            <LoadingScreen />
        )
    }

    return (
        <>
            <div className="min-h-screen bg-gradient-to-br">
                <div className="container mx-auto px-4 py-16">
                    <div className="text-center mb-12">
                        <h1 className="text-5xl md:text-6xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600 mb-6">
                            Payment Summary
                        </h1>
                        <p className="text-xl text-gray-600 max-w-2xl mx-auto">
                            Review your seat booking details before proceeding to payments
                        </p>
                    </div>

                    <div className="max-w-6xl mx-auto grid gap-6 grid-cols-1 lg:grid-cols-3">

                        <div className="lg:col-span-2 bg-white rounded-xl shadow-md p-6 md:p-8">
                            <h2 className="text-xl md:text-2xl font-semibold text-center mb-6">{event.title}</h2>
                            <div className="mb-6">
                                <h3 className="font-semibold mb-1">Event Description</h3>
                                <p className="text-gray-700 text-sm md:text-base">
                                    {event.description}
                                </p>
                                <p className="mt-4 text-sm md:text-base">
                                    <strong>Venue Located At:</strong> {event.location}<br />
                                    <strong>Start Time:</strong> {startDate.toLocaleString('en-AU')} <br />
                                    <strong>End Time:</strong> {addMinutes(startDate, event.duration).toLocaleString('en-AU')}
                                </p>
                            </div>

                        </div>

                        <div className="lg:col-span-1 bg-white rounded-xl shadow-md p-6 md:p-8">
                            <h2 className="text-xl md:text-2xl font-semibold text-center mb-6 flex items-center justify-center">
                                <div className="w-6 h-6 md:w-8 md:h-8 bg-blue-500 rounded-full flex items-center justify-center mr-3">
                                    <span className="text-white font-bold text-sm md:text-base">$</span>
                                </div>
                                Cost Summary
                            </h2>


                            <div className="space-y-3 mb-6">

                                {selectedSeats.map((seat, index) => (
                                    <div className="flex justify-between items-center py-2">
                                        <span className="text-gray-600 text-sm md:text-base">Seat {seat.row + 1}-{seat.column + 1} ({seat.tierName})</span>
                                        <span className="font-medium text-gray-800 text-sm md:text-base">${seat.tierPrice}</span>
                                    </div>

                                ))}
                            </div>


                            <div className="border-t-2 border-blue-100 pt-4">
                                <div className="flex justify-between items-center">
                                    <span className="text-lg font-bold text-gray-800">Total</span>
                                    <span className="text-xl md:text-2xl font-bold text-blue-600">${totalPrice}</span>
                                </div>
                            </div>


                        </div>
                    </div>


                    <div className="max-w-2xl mx-auto mt-12 flex flex-col sm:flex-row gap-4 justify-center">
                        <button onClick={goNext} className="bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-xl px-10 py-4 text-lg font-semibold hover:from-blue-600 hover:to-indigo-700 transform hover:scale-105 transition-all duration-300 shadow-lg">
                            Complete Payment
                        </button>
                        <button onClick={goBack} className="border-2 border-blue-500 text-blue-600 rounded-xl px-10 py-4 text-lg font-semibold hover:bg-blue-50 transform hover:scale-105 transition-all duration-300">
                            Back to Seat Selection
                        </button>
                    </div>

                </div>
            </div>
        </>
    )
}

export default SummaryPayment