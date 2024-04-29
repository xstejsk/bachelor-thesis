import AspectRatio from "@mui/joy/AspectRatio";
import Card from "@mui/joy/Card";
import CardContent from "@mui/joy/CardContent";
import CardOverflow from "@mui/joy/CardOverflow";
import Typography from "@mui/joy/Typography";
import Button from "@mui/joy/Button";
import CardActions from "@mui/joy/CardActions";
import { Calendar } from "../models/calendar";
import { Link } from "react-router-dom";
import LocationOnIcon from "@mui/icons-material/LocationOn";

type LessonCardProps = {
  lesson: Calendar;
};

export default function LessonCard(props: LessonCardProps) {
  return (
    <Card variant="outlined" sx={{ width: 300, height: 280 }}>
      <CardOverflow>
        <AspectRatio ratio="2">
          <img
            src={
              props.lesson.thumbnail
                ? "data:image/png;base64, " + props.lesson.thumbnail
                : "/calendar-default.jpg"
            }
            // srcSet="https://images.unsplash.com/photo-1532614338840-ab30cf10ed36?auto=format&fit=crop&w=318&dpr=2 2x"
            loading="lazy"
            alt=""
          />
        </AspectRatio>
      </CardOverflow>
      <CardContent>
        <Typography level="title-lg">{props.lesson.name}</Typography>
        <Typography startDecorator={<LocationOnIcon />} level="title-md">
          {props.lesson.location.name}
        </Typography>
      </CardContent>

      <CardActions>
        <Link
          to={`/calendars/${props.lesson.id}/events?minTime=${props.lesson.minTime}&maxTime=${props.lesson.maxTime}`}
          style={{
            textDecoration: "none",
            width: "100%",
          }}
        >
          <Button
            variant="solid"
            size="sm"
            sx={{
              width: "100%",
            }}
          >
            Rozvrh
          </Button>
        </Link>
      </CardActions>
    </Card>
  );
}
