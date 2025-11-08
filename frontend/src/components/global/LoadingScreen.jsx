import React, { useState, useEffect } from 'react';


const funFacts = [
  "Letting guests choose their own seats can boost satisfaction by over 30%?",
  "80% of attendees remember events where they had some control over their experience?",
  "Digital RSVPs can reduce no-shows by up to 20%?",
  "Strategic seating can improve networking and guest interaction?",
  "Photo booths increase social media sharing by 50% at events?",
  "Events with clear signage reduce confusion and improve guest flow?",
  "Most guests decide whether they’ll stay at an event within the first 10 minutes?",
  "Offering dietary options makes your event feel 3x more inclusive?",
  "A simple welcome message can set the tone for the entire event?",
  "Assigned seating is great for formal events — open seating works better for casual vibes?",
  "Sending a reminder 24 hours before your event can increase attendance by up to 15%?",
  "Events with interactive elements (like polls or games) have higher engagement?",
  "A well-placed coffee cart can turn a quiet room into a buzzing space?",
  "Having a backup plan for weather or tech saves stress and keeps things running?",
  "You don't need a huge budget — thoughtful planning makes the biggest impact?"
];

const LoadingScreen = ({ 
  title = "Loading...", 
  subtitle = "Please wait a moment",
  spinnerSize = "h-16 w-16",
  backgroundColor = "bg-gradient-to-br from-blue-50 to-indigo-100"
}) => {
  const [factIndex, setFactIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setFactIndex((prevIndex) => (prevIndex + 1) % funFacts.length);
    }, 5000); 

    return () => clearInterval(interval); 
  }, []);

  return (
    <div className={`min-h-screen ${backgroundColor} flex items-center justify-center`}>
      <div className="text-center px-4">
        <div className={`animate-spin rounded-full ${spinnerSize} border-b-4 border-blue-500 mx-auto mb-4`}></div>
        <p className="text-xl text-gray-600 font-medium">{title}</p>
        <p className="text-m text-gray-500">{subtitle}<br/>Did you know that: {funFacts[factIndex]}</p>


      </div>
    </div>
  );
};

export default LoadingScreen;
