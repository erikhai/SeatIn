import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import useAuth from '../../components/auth/UseAuth';
import LoadingScreen from '../../components/global/LoadingScreen';
import axios from 'axios';
import { addMinutes } from 'date-fns';

const Ticket = () => {
    const isLoggedIn = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [isLoading, setIsLoading] = useState(true);
    const [qrImageUrl, setQrImageUrl] = useState(null);
    const { event, selectedSeats, totalPrice } = location.state || {};
    const BASE_URL = process.env.REACT_APP_BASE_URL;



    const generateQrCode = async () => {
        
        try {
            const text = `Event: ${event.title}\nLocation: ${event.location}\nStart Time: ${(new Date(event.start)).toLocaleString('en-AU')}\nEnd Time: ${addMinutes((new Date(event.start)).toLocaleString('en-AU'), event.duration).toLocaleString('en-AU')}\nSeats: ${selectedSeats.map(seat => `(${seat.row + 1},${seat.column + 1})`).join(', ')}\nTotal Paid: $${totalPrice}`;
            const token = localStorage.getItem("accessToken");
            const response = await axios.post(
                `${BASE_URL}/ticket/code-generation`,
                { text },
                {
                    headers: { Authorization: `Bearer ${token}` },
                    responseType: 'blob'
                }
            );
            const imageBlob = response.data;
            const imageObjectURL = URL.createObjectURL(imageBlob);
            setQrImageUrl(imageObjectURL);
          
        } catch (error) {
            console.error('QR generation or email error:', error);
 
        }
    };


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
            setIsLoading(false);
        }


        const init = async () => {
            await generateQrCode();
            setIsLoading(false);
        };

        init();
    }, [isLoggedIn]);

    useEffect(() => {
        if (isLoggedIn === null || isLoggedIn === false) return;
        console.log(event, selectedSeats, totalPrice);
        console.log("location.state ticket: ", location.state)
        

        /* From the viewing their ticket, they cannot go back to previous pages
        as in the real world, that reflects them paying for the ticket. So I set it that if
        you go back, you go back to home page */

        window.history.pushState(null, '', window.location.pathname);

        const handlePopState = () => {
            navigate('/');
        };

        window.addEventListener('popstate', handlePopState);

        return () => {
            window.removeEventListener('popstate', handlePopState);
        };
        
    }, [isLoggedIn, navigate, event, selectedSeats, totalPrice]);

    if (isLoggedIn === null || isLoading) {
        return <LoadingScreen />;
    }

    const goToEvents = () => {
        navigate('/auth/events');
    };

    return (
        <>
            <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-indigo-100">
                <div className="container mx-auto px-4 py-8">

                    <div className="max-w-4xl mx-auto mb-8 space-y-4">
                        <div className="bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-xl p-6 shadow-lg">
                            <div className="flex items-center justify-center">
                                <div className="w-8 h-8 bg-white rounded-full flex items-center justify-center mr-4">
                                    <svg className="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                                    </svg>
                                </div>
                                <div className="text-center">
                                    <h1 className="text-2xl md:text-3xl font-bold mb-1">Payment Successful!</h1>
                                    <p className="text-green-100">Your seats have been reserved for {event.title}</p>
                                </div>
                            </div>
                        </div>


                        <div className="bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-xl p-4 shadow-lg">
                            <div className="flex items-center justify-center">
                                <div className="w-6 h-6 bg-white rounded-full flex items-center justify-center mr-3">
                                    <svg className="w-4 h-4 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                                        <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
                                        <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
                                    </svg>
                                </div>
                                <span className="text-lg font-semibold">Confirmation & QR Code sent to your email (check spam if email cannot be found)</span>
                            </div>
                        </div>
                    </div>

                    <div className="max-w-4xl mx-auto">
                        <div className="bg-white rounded-2xl shadow-xl overflow-hidden">

                     


                            <div className="p-8 text-center">

                                <div className="max-w-sm mx-auto">
                                    <div className="flex items-center justify-center min-h-[300px]">
                                        <img src={qrImageUrl} alt="QR CODE" />
                                    </div>
                                </div>

                                <div className="mt-6 text-center">
                                    <div className="bg-blue-50 rounded-lg p-4 max-w-md mx-auto">
                                        <p className="text-sm text-blue-800">
                                            <strong>Important:</strong> Present this QR code at the venue entrance.
                                            A copy has also been sent to your registered email address.
                                        </p>
                                    </div>
                                </div>
                            </div>


                            <div className="bg-gray-50 p-8">
                                <h3 className="text-xl font-bold text-gray-800 mb-4">Event Details</h3>
                                <div className="grid md:grid-cols-2 gap-6">
                                    <div>
                                        <div className="mb-4">
                                            <h4 className="font-semibold text-gray-700">Venue</h4>
                                            <p className="text-gray-600">{event.location}</p>
                                        </div>

                                        <div>
                                            <h4 className="font-semibold text-gray-700">Your Seats</h4>
                                            {selectedSeats.map((seat, index) => (
                                                <div className="flex justify-between items-center py-2 ">
                                                    <span className="text-gray-600 text-sm">Seat {seat.row + 1}-{seat.column + 1} for ${seat.tierPrice}</span>
                                                </div>

                                            ))}
                                        </div>
                                    </div>
                                    <div>
                                        <div className="mb-4">
                                            <h4 className="font-semibold text-gray-700">Total Paid</h4>
                                            <p className="text-2xl font-bold text-green-600">${totalPrice}</p>
                                        </div>
                                        <div>
                                            <h4 className="font-semibold text-gray-700">Need Help?</h4>
                                            <p className="text-gray-600 text-sm">
                                                Contact event support if you need assistance or have any questions about your booking.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <div className="p-8 text-center border-t border-gray-200">
                                <button
                                    onClick={goToEvents}
                                    className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl px-8 py-3 text-lg font-semibold hover:from-indigo-700 hover:to-purple-700 transform hover:scale-105 transition-all duration-300 shadow-lg"
                                >
                                    Browse More Events
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Ticket;