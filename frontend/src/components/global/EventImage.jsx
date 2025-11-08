import React from "react";

function EventImage({ base64Image }) {
  return (
    <img 
      src={`data:image/jpeg;base64,${base64Image}`} 
      alt="Event" 
      style={{ width: '200px', height: 'auto' }}
    />
  );
}

export default EventImage;