import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';
import BookingSeat from '../../components/global/BookingSeat';

const BookPage = () => {
  const BASE_URL = process.env.REACT_APP_BASE_URL;
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const eventId = queryParams.get('id');

  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [tiers, setTiers] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSeats = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const res = await axios.get(`${BASE_URL}/seat/get-every-seat-for-event`, {
          headers: { Authorization: `Bearer ${token}` },
          params: { eventId },
        });

        const seatData = res.data;
        console.log('Fetched seats:', seatData);
        setSeats(seatData);

        const uniqueTiers = {};
        seatData.forEach((s) => {
          if (s.tierName !== 'Stage') uniqueTiers[s.tierName] = s.tierPrice;
        });
        setTiers(Object.entries(uniqueTiers));
        setLoading(false);
      } catch (err) {
        console.log('Error fetching seats...', err);
      }
    };
    fetchSeats();
  }, [BASE_URL, eventId]);

  const handleToggleSeat = (seat, selected) => {
    setSelectedSeats((prev) =>
      selected ? [...prev, seat] : prev.filter((s) => s !== seat)
    );
  };

  if (loading) return <div>Loading seats...</div>;

  // Determine the grid layout (rows and columns)
  const rows = [...new Set(seats.map((s) => s.row))].sort((a, b) => a - b);
  const cols = [...new Set(seats.map((s) => s.column))].sort((a, b) => a - b);

  const maxRow = Math.max(...rows);
  const maxCol = Math.max(...cols);

  // Create a full grid including missing rows and columns
  const grid = Array.from({ length: maxRow + 1 }, (_, r) =>
    Array.from({ length: maxCol + 1 }, (_, c) =>
      seats.find((s) => s.row === r && s.column === c)
    )
  );

  const totalPrice = selectedSeats.reduce((sum, s) => sum + s.tierPrice, 0);

  const handleProceed = () => {
    console.log('Proceeding with seats:', selectedSeats);
    navigate('/auth/summary-payment', {
      state: { selectedSeats, totalPrice, eventId },
    });
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        padding: 50,
        gap: 100,
        minHeight: '100vh',
        backgroundColor: '#f8fafc',
      }}
    >
      {/* Seat Grid */}
      <div style={{ flex: 1, textAlign: 'center' }}>
        <h1 style={{ marginBottom: 40, color: '#334155' }}>Select Your Seats</h1>

        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 20,
          }}
        >
          {grid.map((rowSeats, rowIndex) => (
            <div
              key={rowIndex}
              style={{
                display: 'grid',
                gridTemplateColumns: `repeat(${maxCol + 1}, 70px)`,
                justifyContent: 'center',
                gap: 14,
                opacity: rowSeats.some((s) => s) ? 1 : 0.2,
              }}
            >
              {rowSeats.map((seat, colIndex) =>
                seat ? (
                  <BookingSeat
                    key={`${rowIndex}-${colIndex}`}
                    seat={seat}
                    onToggle={handleToggleSeat}
                    size={70}
                  />
                ) : (
                  <div key={`${rowIndex}-${colIndex}`} style={{ width: 70, height: 70 }} />
                )
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Sidebar */}
      <div
        style={{
          minWidth: 300,
          background: '#fff',
          padding: 30,
          borderRadius: 12,
          boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
          height: 'fit-content',
          alignSelf: 'start',
        }}
      >
        <h2 style={{ color: '#1e293b', marginBottom: 20 }}>Seat Tiers</h2>
        {tiers.map(([name, price]) => (
          <div
            key={name}
            style={{
              marginBottom: 10,
              fontSize: 18,
              color: '#475569',
              display: 'flex',
              justifyContent: 'space-between',
            }}
          >
            <strong>{name}</strong> <span>${price}</span>
          </div>
        ))}

        <hr style={{ margin: '20px 0', borderColor: '#e2e8f0' }} />

        <h2 style={{ color: '#1e293b', marginBottom: 10 }}>Selected Seats</h2>
        {selectedSeats.length > 0 ? (
          <ul style={{ fontSize: 16, color: '#334155' }}>
            {selectedSeats.map((s) => (
              <li key={`${s.row}-${s.column}`} style={{ marginBottom: 5 }}>
                Row {s.row + 1}, Col {s.column + 1} ({s.tierName}) – ${s.tierPrice}
              </li>
            ))}
          </ul>
        ) : (
          <p style={{ color: '#94a3b8' }}>None selected</p>
        )}

        <h2 style={{ marginTop: 20, color: '#1e293b' }}>
          Total: ${totalPrice}
        </h2>

        <button
          onClick={handleProceed}
          style={{
            marginTop: 25,
            width: '100%',
            padding: '15px 0',
            fontSize: 18,
            backgroundColor: '#00bcd4',
            color: '#fff',
            border: 'none',
            borderRadius: 8,
            cursor: selectedSeats.length === 0 ? 'not-allowed' : 'pointer',
            opacity: selectedSeats.length === 0 ? 0.6 : 1,
            transition: 'background 0.3s',
          }}
          disabled={selectedSeats.length === 0}
        >
          Proceed
        </button>
      </div>
    </div>
  );
};

export default BookPage;