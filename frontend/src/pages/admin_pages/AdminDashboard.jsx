import React, { useEffect, useState } from 'react';
import useAuthAdmin from '../../components/auth/UseAuthAdmin';
import useAuth from '../../components/auth/UseAuth';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const isLoggedIn = useAuth();
    const isAdmin = useAuthAdmin();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [userSearch, setUserSearch] = useState('');
    const [eventSearch, setEventSearch] = useState('');
    const [eventDetails, setEventDetails] = useState({});
    const [userDetails, setUserDetails] = useState({});

    const updateUserSearch = (event) => {
        setUserSearch(event.target.value);
    }

    const updateEventSearch = (event) => {
        setEventSearch(event.target.value);
    }

    const deleteEvent = async(e) =>{
        e.preventDefault()
        try {

            const token = localStorage.getItem('accessToken');
            const res = await axios.post(`${BASE_URL}/event/delete-event`, {}, {
            headers: { 'Authorization': `Bearer ${token}` },
            params: { eventId: eventDetails.id }
            });

            alert("Event Cancelled")
            setEventDetails({});
        } catch (err) {
            console.log("Error fetching seats...", err);
        }
        };
    const searchItem = (isUserSearch) => {
        const searchTerm = (isUserSearch) ? userSearch : eventSearch;
        if (!searchTerm.trim()) {
            alert("Please enter a search term");
            return;
        }
        const token = localStorage.getItem('accessToken');
        const searchUrl = (isUserSearch) ? "search-user" : "search-event";
        axios.get(`${BASE_URL}/admin/${searchUrl}/${searchTerm}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then((response) => {
                console.log(response.data);
                if (isUserSearch) {
                    if (response.data.userId === -1) {
                        alert("The user you have searched for does not exist");
                    } else {
                        setUserDetails(response.data || {});
                    }
                } else {
                    if (response.data.id === -1) {
                        alert("The event you have searched for does not exist");
                    } else {
                        setEventDetails(response.data || {});
                    }
                }
            })
            .catch(() => {
                alert("Error fetching the requested Event")
            });
    }

    useEffect(() => {
        // Wait for authentication to be checked
        if (isLoggedIn === null || isAdmin === null) {
            console.log("Still checking authentication...");
            return; // Don't do anything until we know the auth state
        }
    
        if (isLoggedIn === false) {
            console.log("User is not logged in");
            navigate("/");
            return;
        }
    
        if (isAdmin === false) {
            console.log("User is logged in but not an admin");
            window.alert("Permission Denied");
            navigate("/");
            return;
        }
    
        // If we reach here, user is logged in AND is admin
        console.log("User is authenticated as admin");
        setIsLoading(false);
    }, [isLoggedIn, isAdmin, navigate]);

    return (
        <>
            <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100 px-4 py-12">
                <div className="max-w-6xl mx-auto">
                    
                    {/* Header */}
                    <div className="text-center mb-12">
                        <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-2xl mb-6 shadow-lg">
                            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                            </svg>
                        </div>
                        <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent mb-3">
                            Admin Dashboard
                        </h1>
                        <p className="text-gray-600 text-lg">Search and manage users and events</p>
                    </div>

                    <div className="grid md:grid-cols-2 gap-8">
                        
                        {/* User Search Section */}
                        <div className="bg-white/80 backdrop-blur-xl rounded-3xl shadow-2xl border border-white/20 p-8">
                            <div className="flex items-center mb-6">
                                <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center mr-3">
                                    <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                    </svg>
                                </div>
                                <h2 className="text-2xl font-bold text-gray-800">User Search</h2>
                            </div>

                            <div className="space-y-4 mb-6">
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                        </svg>
                                    </div>
                                    <input
                                        type='text'
                                        value={userSearch}
                                        onChange={updateUserSearch}
                                        placeholder="Search by username"
                                        className="w-full pl-12 pr-4 py-4 bg-gray-50/50 border border-gray-200 rounded-2xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all duration-300"
                                    />
                                </div>
                                <button
                                    onClick={() => searchItem(true)}
                                    className="w-full bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold py-4 px-6 rounded-2xl hover:from-blue-600 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-blue-500/20 transform hover:scale-[1.02] transition-all duration-300 shadow-lg hover:shadow-xl"
                                >
                                    Search User
                                </button>
                            </div>

                            {Object.keys(userDetails).length > 0 ? (
                                <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl p-6 space-y-3 border border-blue-100">
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-blue-600 w-24">User ID:</span>
                                        <span className="text-gray-800 flex-1">{userDetails.userId}</span>
                                    </div>
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-blue-600 w-24">Username:</span>
                                        <span className="text-gray-800 flex-1">{userDetails.userName}</span>
                                    </div>
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-blue-600 w-24">Email:</span>
                                        <span className="text-gray-800 flex-1">{userDetails.email}</span>
                                    </div>
                                </div>
                            ) : (
                                <div className="text-center py-8 text-gray-500 bg-gray-50/50 rounded-2xl border border-gray-200">
                                    <svg className="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                                    </svg>
                                    <p className="font-medium">No users searched</p>
                                    <p className="text-sm mt-1">Search for a user to see details</p>
                                </div>
                            )}
                        </div>

                        {/* Event Search Section */}
                        <div className="bg-white/80 backdrop-blur-xl rounded-3xl shadow-2xl border border-white/20 p-8">
                            <div className="flex items-center mb-6">
                                <div className="w-10 h-10 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center mr-3">
                                    <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                </div>
                                <h2 className="text-2xl font-bold text-gray-800">Event Search</h2>
                            </div>

                            <div className="space-y-4 mb-6">
                                <div className="relative">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                                        </svg>
                                    </div>
                                    <input
                                        type='text'
                                        value={eventSearch}
                                        onChange={updateEventSearch}
                                        placeholder="Search by event name"
                                        className="w-full pl-12 pr-4 py-4 bg-gray-50/50 border border-gray-200 rounded-2xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all duration-300"
                                    />
                                </div>
                                <button
                                    onClick={() => searchItem(false)}
                                    className="w-full bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-semibold py-4 px-6 rounded-2xl hover:from-indigo-600 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 transform hover:scale-[1.02] transition-all duration-300 shadow-lg hover:shadow-xl"
                                >
                                    Search Event
                                </button>
                            </div>

                            {Object.keys(eventDetails).length > 0 ? (
                                <div className="bg-gradient-to-br from-indigo-50 to-purple-50 rounded-2xl p-6 space-y-3 border border-indigo-100">
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-indigo-600 w-32">Event ID:</span>
                                        <span className="text-gray-800 flex-1">{eventDetails.id}</span>
                                    </div>
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-indigo-600 w-32">Event Name:</span>
                                        <span className="text-gray-800 flex-1">{eventDetails.eventName}</span>
                                    </div>
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-indigo-600 w-32">Organiser:</span>
                                        <span className="text-gray-800 flex-1">{eventDetails.eventOrganiser}</span>
                                    </div>
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-indigo-600 w-32">Description:</span>
                                        <span className="text-gray-800 flex-1">{eventDetails.eventDescription}</span>
                                    </div>
                                    <div className="flex items-start">
                                        <span className="text-sm font-semibold text-indigo-600 w-32">Duration:</span>
                                        <span className="text-gray-800 flex-1">{eventDetails.duration}</span>
                                    </div>
                                    <div className="flex justify-center mt-6">
                                        <button
                                        onClick={deleteEvent}
                                        className="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-6 rounded-md shadow-md transition duration-200"
                                        >
                                        Cancel Event
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <div className="text-center py-8 text-gray-500 bg-gray-50/50 rounded-2xl border border-gray-200">
                                    <svg className="w-12 h-12 text-gray-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                                    </svg>
                                    <p className="font-medium">No events searched</p>
                                    <p className="text-sm mt-1">Search for an event to see details</p>
                                </div>
                            )}
                        </div>

                    </div>
                </div>
            </div>
        </>
    )
}

export default AdminDashboard;
