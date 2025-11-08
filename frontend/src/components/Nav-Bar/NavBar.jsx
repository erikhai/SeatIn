import seatinIcon from '../../seatin-icon.svg';
import { useEffect, useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function NavBar() {
  const { signedIn, setSignedIn, logout } = useAuth();
  const [scrolled, setScrolled] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 4);
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, []);


  useEffect(() => {
    setSignedIn(!!localStorage.getItem("accessToken"));
  }, [location, setSignedIn]);

  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === "accessToken") setSignedIn(!!e.newValue);
    };
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, [setSignedIn]);

  const linkBase = "px-3 py-2 text-sm font-medium transition-colors duration-150";
  const active = "text-white font-semibold";
  const inactive = "text-blue-100 hover:text-white";

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header
      className={`hidden md:block sticky top-0 z-50 w-full border-b border-blue-700 transition-colors duration-300 ${
        scrolled ? "bg-blue-700 shadow-sm" : "bg-blue-600"
      }`}
      role="banner"
    >
      <nav
        className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 flex h-16 items-center justify-between"
        aria-label="Main"
      >
        <div className="flex items-center gap-3">
          <Link to="/" className="flex items-center gap-2" aria-label="Home">
            <img
            src={seatinIcon}
            alt="SeatIn"
            className="h-8 w-8 rounded-lg bg-white object-cover"
          />
            <span className="text-lg font-semibold tracking-tight text-white">SeatIn</span>
          </Link>
        </div>

        <div className="flex items-center gap-2">
          <NavLink to="/" className={({ isActive }) => `${linkBase} ${isActive ? active : inactive}`}>
            Home
          </NavLink>

          {signedIn && (
            <NavLink
              to="/auth/events"
              className={({ isActive }) => `${linkBase} ${isActive ? active : inactive}`}
            >
              Events
            </NavLink>
          )}

          {signedIn && (
            <NavLink
              to="/auth/dashboard"
              className={({ isActive }) => `${linkBase} ${isActive ? active : inactive}`}
            >
              Dashboard
            </NavLink>
          )}
        </div>

        <div>
          {!signedIn ? (
            <NavLink
              to="/login"
              className="rounded-xl border border-white px-4 py-2 text-sm font-semibold text-white hover:bg-white hover:text-blue-700 transition"
            >
              Login
            </NavLink>
          ) : (
            <button
              onClick={handleLogout}
              className="rounded-xl border border-white px-4 py-2 text-sm font-semibold text-white hover:bg-white hover:text-blue-700 transition"
            >
              Logout
            </button>
          )}
        </div>
      </nav>
    </header>
  );
}
