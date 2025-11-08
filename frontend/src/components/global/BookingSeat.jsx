import React, { useState } from "react";

export default function BookingSeat({ seat, onToggle, size = 60 }) {
  const [selected, setSelected] = useState(false);
  const { row, column, tierColour, reservedBy, tierName } = seat;

  const isReserved = reservedBy > 0;
  const isStage = tierName === "Stage";

  const background = isReserved
    ? "#555"
    : isStage
    ? "#888"
    : tierColour || "#ccc";

  const handleClick = () => {
    if (isReserved || isStage) return;
    setSelected(!selected);
    onToggle(seat, !selected);
  };

  return (
    <div
      onClick={handleClick}
      style={{
        width: size,
        height: size,
        background: selected ? "#00bcd4" : background,
        border: "2px solid #333",
        borderRadius: 8,
        cursor: isReserved || isStage ? "not-allowed" : "pointer",
        opacity: isReserved ? 0.5 : 1,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontSize: size / 4,
        fontWeight: 'bold',
        color: "#fff",
        userSelect: "none",
      }}
      title={`${tierName} - Row ${row + 1}, Col ${column + 1}`}
    >
      {isStage ? "Stage" : `${tierName}`}
    </div>
  );
}
