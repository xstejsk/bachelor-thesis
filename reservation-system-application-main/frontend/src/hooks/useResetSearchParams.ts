import { useSearchParams } from "react-router-dom";

export const useResetSearchParams = (keepParams: string[]) => {
  const [searchParams, setSearchParams] = useSearchParams();

  const reset = () => {
    const params = searchParams.toString().split("&");
    const newParams = params.filter((param) => {
      const [key] = param.split("=");
      return keepParams?.includes(key) ?? false;
    });
    setSearchParams(
      newParams.join("&") + "&page=0" + "&size=" + searchParams.get("size") ??
        "10"
    );
    // setSearchParams(newParams.join("&"));

    // setSearchParams({ page: "0", size: searchParams.get("size") ?? "10", });
  };
  return { reset };
};
