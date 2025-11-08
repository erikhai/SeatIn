import { createContext, useContext, useEffect, useMemo, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [signedIn, setSignedIn] = useState(() => !!localStorage.getItem("accessToken"));

  // Keep state in sync if any tab updates localStorage
  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === "accessToken") {
        setSignedIn(!!e.newValue);
      }
    };
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  const login = (token) => {
    localStorage.setItem("accessToken", token);
    setSignedIn(true);
    window.dispatchEvent(new StorageEvent("storage", { key: "accessToken", newValue: token }));
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    setSignedIn(false);
    window.dispatchEvent(new StorageEvent("storage", { key: "accessToken", newValue: null }));
  };

  const value = useMemo(() => ({ signedIn, setSignedIn, login, logout }), [signedIn]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
