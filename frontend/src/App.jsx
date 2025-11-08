
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import NavBar from "./components/Nav-Bar/NavBar";
import { AuthProvider } from "./context/AuthContext";
import AdminDashboard from "./pages/admin_pages/AdminDashboard";
import AdminLogin from "./pages/admin_pages/AdminLogin";
import Landing from "./pages/general_pages/Landing";
import Login from "./pages/general_pages/Login";
import NotFound from "./pages/general_pages/NotFound";
import Signup from "./pages/general_pages/Signup";
import Analytics from "./pages/user_pages/Analytics";
import CreateEvent from "./pages/user_pages/CreateEvent";
import Dashboard from "./pages/user_pages/Dashboard";
import EventPreview from "./pages/user_pages/EventPreview";
import Events from "./pages/user_pages/Events";
import SummaryPayment from "./pages/user_pages/SummaryPaymentPage";
import Ticket from "./pages/user_pages/Ticket";
import ViewCreatedEvents from "./pages/user_pages/ViewCreatedEvents";
import ViewRegisteredEvents from "./pages/user_pages/ViewRegisteredEvent";
import ViewRegisteredEventDescription from "./pages/user_pages/ViewRegisteredEventDescription";
import BookPage from "./pages/user_pages/BookPage";



const App = () => {
  return (

    <AuthProvider>
      <Router>
        <NavBar />


        
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />


            <Route path="/auth/summary-payment" element={<SummaryPayment />} />
            <Route path="/auth/ticket" element={<Ticket />} />
            <Route path="/auth/view-registered-events" element={<ViewRegisteredEvents />} />
            <Route
              path="/auth/view-registered-events/description"
              element={<ViewRegisteredEventDescription />}
            />
            <Route path="/auth/view-created-events" element={<ViewCreatedEvents />} />
            <Route path="/auth/analytics" element={<Analytics />} />
            <Route path="/auth/create-event" element={<CreateEvent />} />
            <Route path="/auth/dashboard" element={<Dashboard />} />
            <Route path="/auth/view-event-description" element={<EventPreview />} />
            <Route path="/auth/events" element={<Events />} />
            <Route path="/book" element={<BookPage />} />

            <Route path="/admin/login" element={<AdminLogin />} />
            <Route path="/admin/dashboard" element={<AdminDashboard />} />

            <Route path="/book" element={<BookPage/>}/>


            <Route path="*" element={<NotFound />} />
          </Routes>

      </Router>
    </AuthProvider>
  );
};

export default App;