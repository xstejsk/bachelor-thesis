import { useSearchParams } from "react-router-dom";

type TableControlsProps = {
  isLoading: boolean;
  isError: boolean;
  totalPages: number;
};

export const useTableControls = ({
  isLoading,
  isError,
  totalPages,
}: TableControlsProps) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const currentPage = parseInt(searchParams.get("page")!);

  const handleSetSize = (size: number) => {
    const params = new URLSearchParams(searchParams);
    if (size < 0) {
      import.meta.env.DEBUG && console.log("page size must be positive");
      params.set("size", "0");
    }
    if (size !== null && size !== undefined) {
      params.set("size", size.toString());
      params.set("page", "0");
    }
    setSearchParams(params);
  };

  const handleSetCurrentPage = (page: number) => {
    import.meta.env.DEBUG && console.log("setting page to ", page);
    const params = new URLSearchParams(searchParams);
    if (page < 0 || page >= totalPages) {
      import.meta.env.DEBUG && console.log("page out of range");
      params.set("page", "0");
    }
    if (page !== null && page !== undefined) {
      params.set("page", page.toString());
    }
    setSearchParams(params);
  };

  const handleNextPage = () => {
    if (!isLoading && !isError && currentPage < totalPages - 1) {
      handleSetCurrentPage(currentPage + 1);
    } else {
      import.meta.env.DEBUG && console.log("max page reached");
    }
  };

  const handlePreviousPage = () => {
    if (!isLoading && !isError && currentPage > 0) {
      handleSetCurrentPage(currentPage - 1);
    } else {
      import.meta.env.DEBUG && console.log("min page reached");
    }
  };

  const handleJumpToPage = (page: number) => {
    if (!isLoading && !isError) {
      if (page < 0 || page >= totalPages) {
        import.meta.env.DEBUG && console.log("page out of range");
        return;
      }
      handleSetCurrentPage(page);
    } else {
      import.meta.env.DEBUG && console.log("loading ");
    }
  };

  const resetPage = () => {
    handleSetCurrentPage(0);
  };

  const reachablePages = (range: number) => {
    if (!isLoading && !isError) {
      const pagesLeft = [];
      const pagesRight = [];
      for (let i = 1; i <= range; i++) {
        if (currentPage + i < totalPages) {
          pagesRight.push(currentPage + i);
        }
        if (currentPage - i >= 0) {
          pagesLeft.push(currentPage - i);
        }
      }
      pagesLeft.reverse();

      return [...pagesLeft, currentPage, ...pagesRight];
    }

    return [];
  };

  return {
    handleNextPage,
    handlePreviousPage,
    handleJumpToPage,
    reachablePages,
    setSize: handleSetSize,
    resetPage,
  };
};
