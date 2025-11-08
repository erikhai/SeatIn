import LoadingScreen from '../../components/global/LoadingScreen';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuthAdmin from '../../components/auth/UseAuthAdmin';
import useAuth from '../../components/auth/UseAuth';
const AdminLogin = () => {
    const isLoggedIn = useAuth();
    const isAdmin = useAuthAdmin();
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();
    const [clicked, setClicked] = useState(false);

    const BASE_URL = process.env.REACT_APP_BASE_URL;
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const validateCredentials = async (e) => {
        e.preventDefault();
        setClicked(true);
        try {
            const response = await fetch(`${BASE_URL}/admin/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                let errorMessage = 'Login failed';
                setClicked(false);
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || JSON.stringify(errorData);
                } catch {
                    const errorText = await response.text();
                    if (errorText) errorMessage = errorText;
                }
                throw new Error(errorMessage);
            }

            const data = await response.json();
            console.log(data)


            localStorage.setItem('accessToken', data.token);
            setError('');
            navigate("/admin/dashboard");

        } catch (error) {
            setClicked(false);
            setError('Invalid Details Provided');
        }
    };

    useEffect(() => {
        if (isLoggedIn === null) return;

        if (isLoggedIn === false) {
            setIsLoading(false);
        } else {
            if (isAdmin === false) {
                window.alert("Permission Denied");
                navigate("/");
                return;


                
            } else {
                navigate("/admin/dashboard");
            }
            
        }
    }, [isLoggedIn, isAdmin]);

    if (isLoading || isLoggedIn === null) {
        return <LoadingScreen />;
    }

    return (
        <div className="min-h-screen bg-gray-50">

            <div className="flex items-center justify-center px-4 py-12 sm:px-6 lg:px-8">
                <div className="w-full max-w-md space-y-8">

                    <div className="text-center">
                        <div className="mx-auto h-16 w-16 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 flex items-center justify-center mb-6">
                            <svg className="h-8 w-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                            </svg>
                        </div>
                        <h2 className="text-3xl font-bold tracking-tight text-gray-900">
                            Admin Portal
                        </h2>
                        <p className="mt-2 text-sm text-gray-600">
                            Sign in to manage events and platform settings
                        </p>
                    </div>

                    <div className="bg-white py-8 px-6 shadow-xl rounded-xl border border-gray-100">

                        {error && (
                            <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                                {error}
                            </div>
                        )}

                        <form className="space-y-6" onSubmit={validateCredentials}>
                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                                    Email Address
                                </label>
                                <input
                                    type="email"
                                    id="email"
                                    name="email"
                                    placeholder="admin@events.com"
                                    value={email}
                                    onChange={(e) => {
                                        setEmail(e.target.value);
                                        setError('');
                                    }}
                                    required
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 text-gray-900 placeholder-gray-400"
                                />
                            </div>

                            <div>
                                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                                    Password
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    name="password"
                                    placeholder="Enter your password"
                                    value={password}
                                    onChange={(e) => {
                                        setPassword(e.target.value);
                                        setError('');
                                    }}
                                    required
                                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200 text-gray-900 placeholder-gray-400"
                                />
                            </div>

                            <button
                                type="submit"
                                className="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-blue-600 hover:from-blue-700 hover:to-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200 transform hover:scale-105"
                                disabled={clicked}
                            >
                                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                                </svg>
                                Sign In to Dashboard
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminLogin;
