import { useEffect, useState } from "react"
import axios from "axios"

const useAuthAdmin = () => {
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
        const response = await axios.get(`${BASE_URL}/admin/validate`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        console.log("Are u an admin? ", response.data.isAdmin)

        setIsLoggedIn(response.status === 200 && response.data.isAdmin)
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

export default useAuthAdmin


