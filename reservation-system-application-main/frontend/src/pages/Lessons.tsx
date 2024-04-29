import LessonCard from "../components/LessonCard";
import Grid from "@mui/joy/Grid";
import NewLessonCard from "../components/NewLessonCard";
import { useQuery } from "@tanstack/react-query";
import calendarsApi from "../service/calendarsApi";
import { useRecoilValue } from "recoil";
import authAtom from "../state/authAtom";
import { Role } from "../models/user";
import LoadingCard from "../components/LoadingCard";

export default function SpacingGrid() {
  const auth = useRecoilValue(authAtom);
  const {
    data: calendars,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["calendars"],
    queryFn: () => calendarsApi.getAll(new URLSearchParams()),
  });

  if (isError) {
    return <div>Error</div>;
  }

  return (
    <Grid container className="main" py={2}>
      <Grid xs={12}>
        <Grid container justifyContent="center" spacing={2}>
          {auth.user.role === Role.ADMIN && (
            <Grid>
              <NewLessonCard />
            </Grid>
          )}

          {isLoading &&
            [1, 2, 3].map((i) => (
              <Grid key={i}>
                <LoadingCard />
              </Grid>
            ))}

          {calendars?.content.map((calendar) => (
            <Grid key={calendar.id}>
              <LessonCard lesson={calendar} />
            </Grid>
          ))}
        </Grid>
      </Grid>
    </Grid>
  );
}
