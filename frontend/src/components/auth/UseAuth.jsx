import { useEffect, useState } from "react"
import axios from "axios"

const useAuth = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(null)
  const BASE_URL = process.env.REACT_APP_BASE_URL

  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem("accessToken")

      if (!token) {
        setIsLoggedIn(false)
        return
      }

      try {
        const response = await axios.get(`${BASE_URL}/auth/validate`, {
          headers: { Authorization: `Bearer ${token}` },
        })

        setIsLoggedIn(response.status === 200)
      } catch(error) {
          if (error.response?.status === 401 || error.response?.status === 403) {
            localStorage.removeItem("accessToken")
          }
        setIsLoggedIn(false)
      }
    }

    checkAuth()


    const handleStorageChange = () => checkAuth()
    window.addEventListener("storage", handleStorageChange)
    return () => window.removeEventListener("storage", handleStorageChange)
  }, [BASE_URL])

  return isLoggedIn
}

export default useAuth


