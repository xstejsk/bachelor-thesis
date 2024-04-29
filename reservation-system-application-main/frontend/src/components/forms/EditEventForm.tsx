import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { updateEventSchema } from "./schemas";
import { EditEvent, Event } from "../../models/calendar";
import Button from "@mui/joy/Button";
import Grid from "@mui/joy/Grid";
import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Input from "@mui/joy/Input";
import FormHelperText from "@mui/joy/FormHelperText";
import { Box, Checkbox } from "@mui/joy";
import { UpdateEventParams } from "../../service/eventsApi";

type EventFormProps = {
  event: Event;
  updateEvent: (params: UpdateEventParams) => void;
  closeForm: () => void;
};

const EditEventForm = ({ event, updateEvent, closeForm }: EventFormProps) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<EditEvent>({
    resolver: zodResolver(updateEventSchema),
    defaultValues: {
      title: event.title,
      description: event.description,
      updateSeries: false,
    },
  });

  import.meta.env.DEBUG && console.log(event);
  const onSubmit = (data: EditEvent) => {
    updateEvent({
      id: event.id,
      data,
    });
    closeForm();
    reset();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Grid container spacing={2}>
        <Grid xs={12}>
          <FormControl error={!!errors.title}>
            <FormLabel>Název *</FormLabel>
            <Input
              required
              type="text"
              placeholder="tenisová lekce"
              defaultValue={event.title}
              error={!!errors.title}
              {...register("title", { required: true })}
            />
          </FormControl>
          <FormHelperText>{errors.title?.message}</FormHelperText>
        </Grid>

        <Grid xs={12}>
          <FormControl error={!!errors.description}>
            <FormLabel>Popis</FormLabel>
            <Input
              type="text"
              error={!!errors.description}
              defaultValue={event.description}
              {...register("description", { required: true })}
            />
            <FormHelperText>{errors.description?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12}>
          <FormControl error={!!errors.description}>
            <Checkbox
              {...register("updateSeries", { required: true })}
              label="Aktualizovat celou sérii"
              defaultChecked={false}
              disabled={
                event.recurrence === null ||
                event.recurrence === undefined ||
                event.recurrence.daysOfWeek === null ||
                event.recurrence.daysOfWeek === undefined ||
                event.recurrence.daysOfWeek.length === 0
              }
            />
            <FormHelperText>{errors.description?.message}</FormHelperText>
          </FormControl>
        </Grid>
      </Grid>

      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: 2,
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Button
          sx={{ mt: 1, width: "100%" }}
          type="submit"
          onClick={() => {
            import.meta.env.DEBUG && console.log(errors);
          }}
        >
          Uložit
        </Button>
      </Box>
    </form>
  );
};

export default EditEventForm;
