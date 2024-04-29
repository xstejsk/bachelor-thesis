import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { newEventSchema } from "./schemas";
import { NewEvent } from "../../models/calendar";
import { Controller } from "react-hook-form";
import Button from "@mui/joy/Button";
import Grid from "@mui/joy/Grid";
import { toast } from "react-hot-toast";
import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Input from "@mui/joy/Input";
import FormHelperText from "@mui/joy/FormHelperText";
import { Box } from "@mui/joy";
import DaysSelector from "../DaysSelector";
import Checkbox from "@mui/joy/Checkbox";
import React, { useState } from "react";
import calendarsApi from "../../service/calendarsApi";
import { useParams } from "react-router";
import { format } from "date-fns";
import { useSearchParams } from "react-router-dom";

type EventFormProps = {
  withSubmit: () => void;
};

const EventForm = (props: EventFormProps) => {
  const [repeat, setRepeat] = useState(false);
  const { calendarId } = useParams<{ calendarId: string }>();
  const [searchParams, setSearchParams] = useSearchParams();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    control,
  } = useForm<NewEvent>({
    resolver: zodResolver(newEventSchema),
    defaultValues: {
      title: "",
      description: "",
      date: "",
      startTime: "",
      endTime: "",
      price: 1,
      maximumCapacity: 1,
      recurrence: {
        daysOfWeek: [],
        repeatUntil: format(new Date(), "yyyy-MM-dd"),
      },
    },
  });

  const onSubmit = (data: NewEvent) => {
    import.meta.env.DEBUG && console.log("submitted new event");
    import.meta.env.DEBUG && console.log(data);
    calendarsApi
      .createEvent(calendarId ?? "", data)
      .then((response) => {
        toast.success("Událost vytvořena.");
        import.meta.env.DEBUG && console.log(response);
        updateCalendarTimeRange(data);
        props.withSubmit();
      })
      .catch((err) => {
        import.meta.env.DEBUG && console.log(err);
        toast.error("Událost se nepodařilo vytvořit.");
        props.withSubmit();
      });
    reset();
  };

  const updateCalendarTimeRange = (data: NewEvent) => {
    const params = new URLSearchParams(searchParams);
    if (data.startTime < (searchParams.get("minTime") ?? "00:00")) {
      const newMinTime = data.startTime.substring(0, 3);
      params.set("minTime", newMinTime + "00");
    }
    if (data.endTime > (searchParams.get("maxTime") ?? "23:59")) {
      const newMaxTime = data.endTime.substring(0, 3);
      params.set("maxTime", newMaxTime + "59");
    }
    setSearchParams(params);
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
              error={!!errors.title}
              {...register("title", { required: true })}
            />
          </FormControl>
          <FormHelperText>{errors.title?.message}</FormHelperText>
        </Grid>
        <Grid xs={12} sm={6} md={4}>
          <FormControl error={!!errors.startTime}>
            <FormLabel>Od *</FormLabel>
            <Input
              required
              type="time"
              placeholder="8:00"
              {...register("startTime", { required: true })}
              error={!!errors.startTime}
            />
            <FormHelperText>{errors.startTime?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12} sm={6} md={4}>
          <FormControl error={!!errors.endTime}>
            <FormLabel>Do *</FormLabel>
            <Input
              required
              type="time"
              placeholder="18:00"
              error={!!errors.endTime}
              {...register("endTime", { required: true })}
            />
            <FormHelperText>{errors.endTime?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12} sm={12} md={4}>
          <FormControl error={!!errors.date}>
            <FormLabel>Kdy *</FormLabel>
            <Input
              required
              type="date"
              error={!!errors.date}
              {...register("date", { required: true })}
            />
            <FormHelperText>{errors.date?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12} sm={4}>
          <FormControl error={!!errors.price}>
            <FormLabel>Cena *</FormLabel>
            <Input
              type="number"
              error={!!errors.price}
              {...register("price", { valueAsNumber: true, required: true })}
              endDecorator="Kr."
              slotProps={{
                input: { min: 0, step: 10 },
              }}
            />
            <FormHelperText>{errors.price?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12} sm={4}>
          <FormControl error={!!errors.price}>
            <FormLabel>Cena s Multisport *</FormLabel>
            <Input
              type="number"
              error={!!errors.discountPrice}
              {...register("discountPrice", {
                valueAsNumber: true,
                required: true,
              })}
              defaultValue={0}
              endDecorator="Kr."
              slotProps={{
                input: { min: 0, step: 10 },
              }}
            />
            <FormHelperText>{errors.discountPrice?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12} sm={4}>
          <FormControl error={!!errors.maximumCapacity}>
            <FormLabel>Kapacita *</FormLabel>
            <Input
              type="number"
              error={!!errors.maximumCapacity}
              {...register("maximumCapacity", {
                valueAsNumber: true,
                required: true,
              })}
              slotProps={{
                input: { min: 1 },
              }}
              endDecorator="osob"
            />
            <FormHelperText>{errors.maximumCapacity?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid xs={12}>
          <FormControl error={!!errors.description}>
            <FormLabel>Popis</FormLabel>
            <Input
              type="text"
              error={!!errors.description}
              {...register("description", { required: true })}
            />
            <FormHelperText>{errors.description?.message}</FormHelperText>
          </FormControl>
        </Grid>
        <Grid>
          <FormControl>
            <Checkbox
              label="Opakovat"
              variant="solid"
              onChange={(e) => setRepeat(e.target.checked)}
            />
          </FormControl>
        </Grid>
        {repeat && (
          <React.Fragment>
            <Grid xs={12}>
              <FormControl error={!!errors.description}>
                <Controller
                  name="recurrence.daysOfWeek"
                  control={control}
                  render={({ field }) => (
                    <DaysSelector onChange={field.onChange} />
                  )}
                />
                <FormHelperText>
                  {errors.recurrence?.daysOfWeek?.message}
                </FormHelperText>
              </FormControl>
            </Grid>
            <Grid xs={12}>
              <FormLabel>Opakovat do *</FormLabel>
              <Input
                type="date"
                error={!!errors.recurrence?.repeatUntil}
                {...register("recurrence.repeatUntil", { required: repeat })}
              />
              <FormHelperText>
                {errors.recurrence?.repeatUntil?.message}
              </FormHelperText>
            </Grid>
          </React.Fragment>
        )}
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

export default EventForm;
