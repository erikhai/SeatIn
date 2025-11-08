import axios from "axios";
import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import useAuth from '../../components/auth/UseAuth';


const CreateEvent = () => {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const [page, setPage] = useState(1);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [startTime, setStartTime] = useState(null);
  const [minutes, setMinutes] = useState(0);
  const [photo, setPhoto] = useState(null);
  const [rows, setRows] = useState(1);
  const [columns, setColumns] = useState(1);
  const navigate = useNavigate();

  const [selected, setSelected] = useState(new Set()); // seats selected in page 2
  const [tiers, setTiers] = useState([{ id: '0', name: 'Stage', price: 0, color: '#000000' }, { id: '1', name: 'Free Tier', price: 0, color: "#CA8A04" }]); // [{id, name, price, color}]
  const [newTierName, setNewTierName] = useState("");
  const [newTierPrice, setNewTierPrice] = useState("");
  const [activeTier, setActiveTier] = useState(null);
  const [activeTierColor, setActiveTierColor] = useState("");
  const [seatAssignments, setSeatAssignments] = useState({}); // { "r-c": tierId }
  const isLoggedIn = useAuth();

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    navigate('/');
    setTimeout(() => {
        window.alert('Please ensure you are logged in.');
    }, 500);
};



  useEffect(() => {
    if (isLoggedIn === null) 
      return;

    if (isLoggedIn === false) {
      handleLogout();
      return;
    }
    if (isLoggedIn === true) {

    }


  }, [isLoggedIn]);

  useEffect(() => {
    if (page === 3) {
      const minimumTier = tiers.find(t => t.name === "Free Tier");
      if (!minimumTier) return; // safety

      setSeatAssignments(prev => {
        const updated = { ...prev };
        for (const seatKey of selected) {
          if (!updated[seatKey]) {
            updated[seatKey] = minimumTier.id;
          }
        }
        return updated;
      });
    }
  }, [page, tiers, selected]);

  const [availableTierColors, setAvailableTierColors] = useState([
    "#EF4444", // red-500
    "#F97316", // orange-500
    "#F59E0B", // amber-500
    "#EAB308", // yellow-500
    "#84CC16", // lime-500
    "#22C55E", // green-500
    "#10B981", // emerald-500
    "#14B8A6", // teal-500
    "#06B6D4", // cyan-500
    "#0EA5E9", // sky-500
    "#3B82F6", // blue-500
    "#6366F1", // indigo-500
    "#8B5CF6", // violet-500
    "#A855F7", // purple-500
    "#D946EF", // fuchsia-500
    "#EC4899", // pink-500
    "#F43F5E", // rose-500
    "#9CA3AF", // gray-400
    "#64748B", // slate-500
    "#1F2937", // gray-800
  ]);
  const takenTierColors = [];

  const nextPage = () => {
    if (name === '' || description === '' || location === '' || startTime === null || photo === null) {
      alert("Please fill in all fields before moving on");
      setPage((p) => p);
    } else {
      setPage((p) => p + 1);
    }
  };
  const prevPage = () => setPage((p) => p - 1);

  const handlePhotoChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setPhoto(e.target.files[0]);
    }
  };

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();


    let imageBase64 = null;
    if (photo) {
      imageBase64 = await new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(photo);
        reader.onload = () => resolve(reader.result.split(",")[1]);
        reader.onerror = err => reject(err);
      });
    }


    const seatAssignmentData = Object.entries(seatAssignments).map(([key, tierId]) => {
      const [row, column] = key.split("-").map(Number);
      const tier = tiers.find(t => t.id === tierId);
      return {
        row,
        column,
        tierName: tier?.name || 'Free Tier'
      };
    });


    const tiersForBackend = tiers.map(t => ({
      name: t.name,
      price: parseFloat(t.price),
      color: t.color
    }));


    const startISO = startTime ? new Date(startTime).toISOString().slice(0, 19) : null;


    const eventData = {
      eventName: name,
      description,
      location,
      start: startISO,
      duration: Number(minutes),
      rows,
      columns,
      image: imageBase64,
      selectedSeats: seatAssignmentData,
      tiers: tiersForBackend
    };

    try {
      const token = localStorage.getItem("accessToken");
      const res = await axios.post(`${BASE_URL}/event/create-new-event`, eventData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });
      console.log("Response:", res.data);
      alert("Form submitted successfully!");
      navigate('/auth/view-created-events');
    } catch (err) {
      console.error("Error submitting event:", err);
      alert("Failed to submit event. Check console.");
    }
  };


  const toggleCell = (r, c) => {
    const key = `${r}-${c}`;
    setSelected((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(key)) {
        newSet.delete(key);
      } else {
        newSet.add(key);
      }
      return newSet;
    });
  };

  const getTierColor = () => {
    const randomIndex = Math.floor(Math.random() * availableTierColors.length);
    const randomColor = availableTierColors[randomIndex];
    setAvailableTierColors(availableTierColors.filter((color) => color !== randomColor));
    takenTierColors.push(randomColor);
    return randomColor;
  }

  const invalidTierSelections = () => {
    alert("Please ensure that all tiers have unique names and prices greater than or equal to $0");
  }

  // --- Page 1: Event Info ---
  if (page === 1)
    return (
      <>

        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-6">
          <div className="w-full max-w-4xl bg-white rounded-2xl shadow-lg p-10">
            <h1 className="text-2xl font-bold mb-6 text-gray-800">
              Create Event
            </h1>
            <form onSubmit={handleSubmit} className="space-y-5">
              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Name
                </label>
                <input
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring focus:ring-blue-200"
                  placeholder="Enter event name"
                  required
                />
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Description
                </label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring focus:ring-blue-200"
                  rows="3"
                  placeholder="Enter description"
                ></textarea>
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Location
                </label>
                <input
                  type="text"
                  value={location}
                  onChange={(e) => setLocation(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring focus:ring-blue-200"
                  placeholder="Enter location"
                />
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Start Time
                </label>
                <input
                  type="datetime-local"
                  value={startTime || ""}
                  onChange={(e) => setStartTime(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring focus:ring-blue-200"
                />
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Duration (minutes)
                </label>
                <input
                  type="number"
                  value={(minutes === 0) ? "" : minutes}
                  onChange={(e) => {
                    const finalValue = Math.abs(Number(e.target.value));
                    setMinutes(finalValue);
                  }}
                  className="w-full border border-gray-300 rounded-lg p-3 focus:ring focus:ring-blue-200"
                  min="0"
                />
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Rows: {rows}
                </label>
                <input
                  type="range"
                  min="1"
                  max="10"
                  value={rows}
                  onChange={(e) => setRows(parseInt(e.target.value))}
                  className="w-full accent-blue-600"
                />
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Columns: {columns}
                </label>
                <input
                  type="range"
                  min="1"
                  max="10"
                  value={columns}
                  onChange={(e) => setColumns(parseInt(e.target.value))}
                  className="w-full accent-blue-600"
                />
              </div>

              <div>
                <label className="block text-gray-700 font-medium mb-2">
                  Photo
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handlePhotoChange}
                  className="w-full text-gray-700"
                />
                {photo && (
                  <img
                    src={URL.createObjectURL(photo)}
                    alt="Preview"
                    className="mt-3 h-40 object-cover rounded-lg border"
                  />
                )}
              </div>

              <button
                type="button"
                className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition"
                onClick={nextPage}
              >
                Next →
              </button>
            </form>
          </div>
        </div>
      </>
    );

  // --- Page 2: Select Seat Layout ---
  else if (page === 2)
    return (
      <>
        <h1 className="text-2xl font-bold mb-6 text-gray-800 text-center">
          Select Seat Layout
        </h1>

        <div className="flex justify-center">
          <div
            className="grid gap-2 w-full max-w-[500px]"
            style={{
              gridTemplateColumns: `repeat(${columns}, 1fr)`,
            }}
          >
            {Array.from({ length: rows }).map((_, r) =>
              Array.from({ length: columns }).map((_, c) => {
                const key = `${r}-${c}`;
                const isSelected = selected.has(key);
                return (
                  <div
                    key={key}
                    onClick={() => toggleCell(r, c)}
                    className={`aspect-square flex items-center justify-center rounded-lg border cursor-pointer transition ${isSelected
                        ? "bg-blue-600 border-blue-600"
                        : "bg-gray-100 border-gray-300"
                      }`}
                  ></div>
                );
              })
            )}
          </div>
        </div>

        <div className="mt-6 flex flex-col gap-3 max-w-[500px] mx-auto">
          <button
            type="button"
            className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition"
            onClick={nextPage}
          >
            Next →
          </button>
          <button
            onClick={prevPage}
            className="w-full bg-gray-600 text-white py-3 rounded-lg hover:bg-gray-700 transition"
          >
            ← Back
          </button>
        </div>
      </>
    );

  // --- Page 3: Assign Tiers ---
  else if (page === 3)
    return (
      <div className="min-h-screen p-6 bg-gray-50">
        <div className="flex items-start gap-6">
          {/* Seating Grid */}

          <div className="flex justify-center flex-1">
            <div
              className="grid gap-2 w-full max-w-[500px]"
              style={{ gridTemplateColumns: `repeat(${columns}, 1fr)` }}
            >
              {Array.from({ length: rows }).map((_, r) =>
                Array.from({ length: columns }).map((_, c) => {
                  const key = `${r}-${c}`;
                  const isSeat = selected.has(key);
                  const tierId = seatAssignments[key];
                  const tier = tiers.find((t) => t.id === tierId);

                  return (
                    <div
                      key={key}
                      onClick={() => {
                        if (!isSeat || !activeTier) return;
                        const currTier = tiers.find((t) => t.id === activeTier);
                        const tierColor = currTier.color;
                        console.log(tierColor);
                        setSeatAssignments((prev) => ({
                          ...prev,
                          [key]: activeTier,
                        }));
                        setActiveTierColor(tierColor);
                      }}
                      className={`aspect-square flex items-center justify-center rounded-lg border cursor-pointer transition
                        ${isSeat ? "bg-blue-100" : "bg-gray-200"}
                      `}
                      style={{
                        backgroundColor: tier ? tier.color : isSeat ? "#DBEAFE" : "#E5E7EB", // fallback blue-100 / gray-200
                      }}
                    >
                      {tier ? (
                        <span className="text-xs font-bold text-gray-100">
                          {tier.name}
                        </span>
                      ) : isSeat ? (
                        <span className="text-xs text-gray-500">Seat</span>
                      ) : null}
                    </div>
                  );
                })
              )}
          </div> {/* close inner grid wrapper */}
        </div> {/* close outer flex container */}

          {/* Sidebar */}
          <div className="w-80 bg-white p-4 rounded-lg shadow space-y-4">
            <h2 className="text-lg font-bold">Tiers</h2>

            {/* Add New Tier */}
            <div className="flex flex-col gap-2">
              <input
                type="text"
                placeholder="Tier name"
                value={newTierName}
                onChange={(e) => setNewTierName(e.target.value)}
                className="border rounded p-2"
              />
              <input
                type="number"
                placeholder="Price"
                value={newTierPrice}
                onChange={(e) => setNewTierPrice(e.target.value)}
                className="border rounded p-2"
              />
              <button
                onClick={() => {
                  if (!newTierName || !newTierPrice || parseFloat(newTierPrice) <= 0) {
                    invalidTierSelections();
                    return;
                  }
                  let nameExists = false;
                  for (let i = 0; i < tiers.length; i++) {
                    const currTier = tiers[i];
                    if (currTier.name === newTierName) {
                      nameExists = true;
                      break;
                    }
                  }
                  if (nameExists) {
                    invalidTierSelections();
                    return;
                  }
                  const id = Date.now().toString();
                  const tierColor = getTierColor();
                  setTiers((prev) => [
                    ...prev,
                    { id, name: newTierName, price: parseFloat(newTierPrice), color: tierColor },
                  ]);
                  setNewTierName("");
                  setNewTierPrice("");
                  setActiveTier(id);
                }}
                className="bg-blue-600 text-white py-2 rounded"
              >
                Add Tier
              </button>
            </div>

            {/* Existing Tiers */}
            <ul className="space-y-2">
              {tiers.map((tier) => (
                <li
                  key={tier.id}
                  className={`flex justify-between items-center p-2 rounded cursor-pointer border
                  ${activeTier === tier.id ? "bg-blue-100 border-blue-600" : ""}
                `}
                  onClick={() => setActiveTier(tier.id)}
                >
                  <div>
                    <div
                      className="w-4 h-4 rounded-full border"
                      style={{ backgroundColor: `${tier.color}` }}
                    ></div>

                    {tier.name === 'Stage' ? <span>{tier.name}</span> : <span>{tier.name} (${tier.price})</span>}

                  </div>
                  {(tier.name !== 'Stage' && tier.name !== 'Free Tier') ?
                    <div>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setTiers((prev) => prev.filter((t) => t.id !== tier.id));
                          setSeatAssignments((prev) => {
                            const newAssign = { ...prev };
                            Object.keys(newAssign).forEach((key) => {
                              if (newAssign[key] === tier.id) delete newAssign[key];
                            });
                            return newAssign;
                          });
                          if (activeTier === tier.id) setActiveTier(null);
                        }}
                        className="text-red-500"
                      >
                        ✕
                      </button>
                    </div>
                    : <div>

                    </div>}

                </li>
              ))}
            </ul>

            <div className="flex flex-col gap-2 pt-4">
              <button
                className="bg-green-600 text-white py-2 rounded"
                onClick={handleSubmit}
              >
                Submit Event
              </button>
              <button
                className="bg-gray-600 text-white py-2 rounded"
                onClick={prevPage}
              >
                ← Back
              </button>
            </div>
          </div>
        </div>
      </div>
    );
};

export default CreateEvent;