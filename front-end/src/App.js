import "./App.css";
import axios from "axios";
import { useEffect } from "react";
import { useLocalState } from "./util/LocalStorageUtil";
import { Routes, Route } from "react-router-dom";
import Dashboard from "./Components/Dashboard";
import Homepage from "./Components/Homepage";
import authService from "./authorization/auth-service";
import PrivateRoute from "./PrivateRoute";
import Login from "./Components/Login";
import Register from "./Components/Register";
import Navbar from "./Components/Navbar";

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route element={<PrivateRoute />}>
          <Route path="/dashboard" element={<Dashboard />} />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Homepage />} />
        <Route path="/register" element={<Register />} />
      </Routes>
    </>
  );
}

export default App;
