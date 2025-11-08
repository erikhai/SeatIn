import React from 'react';
import { useNavigate } from 'react-router-dom';


const NotFound = () => {
  const navigate = useNavigate();

  return (
    <>

      <div className="min-h-screen bg-gradient-to-br from-blue-100 via-blue-50 to-indigo-100 flex items-center justify-center px-4">
        <div className="text-center max-w-2xl mx-auto">
          <div className="relative mb-8">
            <h1 className="text-[180px] md:text-[240px] font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-blue-450 to-blue-500 leading-none select-none">
              404
            </h1>
          </div>


          <div className="mb-8 space-y-4">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
              Oops! Page Not Found
            </h2>
            <p className="text-lg text-gray-600 mb-2">
              The page you're looking for does not exist.
            </p>

          </div>


          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center mb-8">
            <button
              onClick={() => navigate('/')}
              className="group relative bg-gradient-to-r from-blue-500 to-blue-500 text-white font-semibold py-4 px-8 rounded-xl text-lg hover:from-blue-600 hover:to-blue-600 transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-xl"
            >
              <span className="relative z-10">Back to Home</span>
              <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-blue-600 to-blue-600 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            </button>

          </div>
        </div>
      </div>
    </>
  );
};

export default NotFound;