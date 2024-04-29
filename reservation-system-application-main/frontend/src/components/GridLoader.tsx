import GridLoader from "react-spinners/GridLoader";

const CustomGridLoader = () => {
  return (
    <div
      style={{
        justifyContent: "center",
        alignItems: "center",
        display: "flex",
        paddingTop: "10vh",
      }}
    >
      <GridLoader
        color={"rgba(44, 122, 123, 0.4)"}
        cssOverride={{
          blockSize: 10,
        }}
        size={20}
        aria-label="Grid Loader"
        data-testid="loader"
      />
    </div>
  );
};

export default CustomGridLoader;
