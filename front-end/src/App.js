import "./App.css";
import { Routes, Route } from "react-router-dom";
import Dashboard from "./Components/Dashboard";
import Homepage from "./Components/Homepage";
import PrivateRoute from "./PrivateRoute";
import Login from "./Components/Login";
import Register from "./Components/Forms/Register";
import Navbar from "./Components/Navbar/Navbar";
import SchedulerWrapper from "./Components/SchedulerWrapper";
import ReservationTableWrapper from "./Components/Tables/ReservationTableWrapper";
import GlobalState from "./util/GlobalState";
import ConfirmRegistration from "./Components/ConfirmRegistration";
import ResetPassword from "./Components/Forms/ResetPassword";
import ConfirmPassword from "./Components/ConfirmPassword";
import AlertTemplate from "react-alert-template-basic";
import { positions, Provider } from "react-alert";
import UsersTableWrapper from "./Components/Tables/UsersTableWrapper";
import Footer from "./Components/footer";

function App() {
  const options = {
    timeout: 5000,
    position: positions.TOP_CENTER,
  };

  return (
    <div className="myWrapper">
      <Provider template={AlertTemplate} {...options}>
        <GlobalState>
          <Navbar />
          <div className="myContent">
            <Routes>
              <Route element={<PrivateRoute />}>
                <Route path="/dashboard" element={<Dashboard />} />
              </Route>
              <Route path="/login" element={<Login />} />
              <Route path="/home" element={<Homepage />} />
              <Route path="/register" element={<Register />} />
              <Route path="/events" element={<SchedulerWrapper />} />
              <Route
                path="/reservations"
                element={<ReservationTableWrapper />}
              />
              <Route path="/users" element={<UsersTableWrapper />} />
              <Route
                path="/registration/confirm"
                element={<ConfirmRegistration />}
              />
              <Route path="/password/reset" element={<ResetPassword />} />
              <Route
                path="/password-reset/confirm"
                element={<ConfirmPassword />}
              />
            </Routes>
          </div>
          <div className="myFooter">
            <Footer />
          </div>
        </GlobalState>
      </Provider>
    </div>
  );
}

export default App;
