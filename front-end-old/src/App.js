import "./App.css";
import { Routes, Route } from "react-router-dom";
import Dashboard from "./Components/Dashboard";
import Homepage from "./Components/Homepage";
import PrivateRoute from "./PrivateRoute";
import Login from "./Components/Login";
import Register from "./Components/Register";
import Navbar from "./Components/Navbar/Navbar";
import CustomScheduler from "./Components/CustomScheduler";
import ReservationsTable from "./Components/Tables/ReservationTable";
import GlobalState from "./util/GlobalState";
function App() {
  return (
    <>
      <GlobalState>
        <Navbar />
        <Routes>
          <Route element={<PrivateRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
          </Route>
          <Route path="/login" element={<Login />} />
          <Route path="/home" element={<Homepage />} />
          <Route path="/register" element={<Register />} />
          <Route path="/events" element={<CustomScheduler />} />
          <Route path="/reservations" element={<ReservationsTable />} />
        </Routes>
      </GlobalState>
    </>
  );
}

export default App;
