import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
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
import ProtectedRoute from "./util/ProtectedRoute";
import NotFound from "./Components/NotFound";

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
              <Route path="/" element={<SchedulerWrapper />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/events" element={<SchedulerWrapper />} />
              <Route
                path="/reservations"
                element={
                  <ProtectedRoute requireAdmin={false}>
                    <ReservationTableWrapper />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/users"
                element={
                  <ProtectedRoute requireAdmin={true}>
                    <UsersTableWrapper />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/registration/confirm"
                element={<ConfirmRegistration />}
              />
              <Route path="/password/reset" element={<ResetPassword />} />
              <Route
                path="/password-reset/confirm"
                element={<ConfirmPassword />}
              />
              <Route path="*" element={<NotFound />} />
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
