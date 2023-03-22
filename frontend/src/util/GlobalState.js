import React, { useEffect, useState } from "react";
const initialState = { user: undefined };

export const Context = React.createContext();

const GlobalState = ({ children }) => {

  const [globalState, setGlobalState] = useState(initialState);
  
  return (
    <Context.Provider value={[globalState, setGlobalState]}>
      {children}
    </Context.Provider>
  );
};

export default GlobalState;
