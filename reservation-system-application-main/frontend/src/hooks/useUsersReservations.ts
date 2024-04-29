import React from "react";
import { useQuery } from "@tanstack/react-query";
import { useRecoilValue, useRecoilState } from "recoil";
import authAtom from "../state/authAtom";
import reservationsAtom from "../state/reservationsAtom";
import reservationsApi from "../service/reservationsApi";

export const useUsersReservations = () => {
  const auth = useRecoilValue(authAtom);
  const [reservations, setReservations] = useRecoilState(reservationsAtom);
  const params = new URLSearchParams();
  params.append("userId", auth.user.id);

  const {
    data: data,
    isLoading: isLoading,
    isError: isError,
    isSuccess: isSuccess,
  } = useQuery({
    queryKey: ["reservations", auth.user.id],
    queryFn: () => reservationsApi.getAll(params),
    enabled: !!auth.user.id,
  });

  const addReservation = (eventId: string) => {
    setReservations([...reservations, eventId]);
  };

  const removeReservation = (eventId: string) => {
    setReservations(
      reservations.filter((reservation) => reservation !== eventId)
    );
  };

  React.useEffect(() => {
    if (isSuccess) {
      setReservations(data.content.map((reservation) => reservation.event.id));
    }
  }, [isSuccess, data, setReservations]);

  return {
    reservations,
    isLoading,
    isError,
    isCallable: !!auth.user.id,
    addReservation,
    removeReservation,
  };
};
