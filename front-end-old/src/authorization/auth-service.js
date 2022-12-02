import axios from "axios";
import { host, loginEndpoint, signUpEndpoint } from "../util/EndpointConfig";
import { useLocalState } from "../util/LocalStorageUtil";

const SignUp = async (email, password) => {
  const [jwt, setJwt] = useLocalState("", "jwt");

  return axios
    .post(host + signUpEndpoint, {
      email,
      password,
    })
    .then((response) => {
      if (response.headers["authorization"]) {
        setJwt(JSON.stringify(response.data));
      }
      return response.data;
    })
    .catch((err) => console.log(err));
};

const Login = async (email, password) => {
  const [jwt, setJwt] = useLocalState("", "jwt");

  return axios
    .post(host + loginEndpoint, {
      email,
      password,
    })
    .then((response) => {
      if (response.headers["authorization"]) {
        setJwt(response.headers["authorization"]);
      }
    });
};

const Logout = () => {
  const [jwt, setJwt] = useLocalState("", "jwt");
  setJwt("");
};

const GetUserJwt = () => {
  const [jwt, setJwt] = useLocalState("", "jwt");
  return JSON.parse(jwt);
};

const authService = {
  SignUp,
  Login,
  Logout,
  GetUserJwt,
};

export default authService;
