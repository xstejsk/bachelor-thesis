import React, { useState, useContext } from "react";
import axios from "axios";
import { useLocalState } from "../util/LocalStorageUtil";
import { host, loginEndpoint } from "../util/EndpointConfig";
import { useLocation, useNavigate } from "react-router-dom";
import { Context } from "../util/GlobalState";

const Login = () => {
  const [username, setusername] = useState("");
  const [password, setPassword] = useState("");
  const loginStatus = "";
  const navigate = useNavigate();
  const location = useLocation();
  const [globalState, setGlobalState] = useContext(Context);

  const handleLogin = async (e) => {
    e.preventDefault();

    axios
      .post(
        host + loginEndpoint,
        {
          username,
          password,
        },
        { withCredentials: true }
      )
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          // const accessToken = response.data["access_token"];
          // const refreshToken = response.data["refresh_token"];
          axios.defaults.headers.common[
            "Authorization"
          ] = `Bearer ${response.data["access_token"]}`;
          console.log(response.data["user"]);
          setGlobalState({ user: response.data["user"] });
          location.state?.from
            ? navigate(location.state.from)
            : navigate("/events");
        } else {
          loginStatus = "failed";
        }
      });
  };

  return (
    <main className="form-signup w-100 m-auto">
      <form onSubmit={handleLogin}>
        <h1 className="h3 mb-3 fw-normal">Přihlašte se</h1>

        <div className="form-floating">
          <input
            type="username"
            className="form-control"
            id="floatingInput"
            placeholder="agata@seznam.cz"
            onChange={(e) => setusername(e.target.value)}
          />
          <label htmlFor="floatingInput">Email</label>
        </div>
        <div className="form-floating">
          <input
            type="password"
            className="form-control"
            id="floatingPassword"
            placeholder="Password"
            onChange={(e) => setPassword(e.target.value)}
          />
          <label htmlFor="floatingPassword">Heslo</label>
        </div>

        <button className="w-100 btn btn-lg btn-primary" type="submit">
          Přihlásit
        </button>
      </form>
    </main>
  );
};

export default Login;
