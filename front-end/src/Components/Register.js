import React from "react";
import { useLocalState } from "../util/LocalStorageUtil";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [controlPassword, setControlPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [jwt, setJwt] = useLocalState("", "jwt");
  const navigate = useNavigate();

  const handleRegistration = () => {};

  return (
    <main className="form-signup w-100 m-auto">
      <form onSubmit={handleRegistration}>
        <h1 className="h3 mb-3 fw-normal">Zaregistrujte se</h1>

        <div className="form-floating">
          <input
            type="email"
            className="form-control"
            id="floatingInput"
            placeholder="agata@seznam.cz"
            onChange={(e) => setEmail(e.target.value)}
          />
          <label htmlFor="floatingInput">Email</label>
        </div>
        <div className="form-floating">
          <input
            type="text"
            className="form-control"
            id="floatingInput"
            placeholder="agata@seznam.cz"
            onChange={(e) => setFirstName(e.target.value)}
          />
          <label htmlFor="floatingInput">Jméno</label>
        </div>
        <div className="form-floating">
          <input
            type="email"
            className="form-control"
            id="floatingInput"
            placeholder="agata@seznam.cz"
            onChange={(e) => setLastName(e.target.value)}
          />
          <label htmlFor="floatingInput">Příjmení</label>
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
        <div className="form-floating">
          <input
            type="password"
            className="form-control"
            id="floatingPassword"
            placeholder="Password"
            onChange={(e) => setControlPassword(e.target.value)}
          />
          <label htmlFor="floatingPassword">Heslo pro kontrolu</label>
        </div>

        <button className="w-100 btn btn-lg btn-primary" type="submit">
          Vytvořit účet
        </button>
      </form>
    </main>
  );
};

export default Register;
