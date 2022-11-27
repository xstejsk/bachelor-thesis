import React from "react";
import GridLoader from "react-spinners/GridLoader";

const CustomGridLoader = (isLoading) => {
  const override = {
    size: 15,
  };
  return (
    <div
      style={{
        justifyContent: "center",
        alignItems: "center",
        display: "flex",
        height: "100vh",
        paddingTop: 100,
        paddingBottom: 200,
      }}
    >
      <GridLoader
        color={"#858585"}
        loading={isLoading}
        cssOverride={override}
        size={20}
        aria-label="Grid Loader"
        data-testid="loader"
      />
    </div>
  );
};

export default CustomGridLoader;
