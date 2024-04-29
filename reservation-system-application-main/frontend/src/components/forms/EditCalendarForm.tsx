import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Grid from "@mui/joy/Grid";
import { useForm, Controller } from "react-hook-form";
import { Location, UpdateCalendarRequest } from "../../models/calendar";
import FormHelperText from "@mui/joy/FormHelperText";
import { Box } from "@mui/joy";
import { zodResolver } from "@hookform/resolvers/zod";
import { newCalenarSchema } from "./schemas";
import ImageUploader from "../ImageUploader";
import calendarsApi from "../../service/calendarsApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-hot-toast";
import { Select } from "@mui/joy";
import { useQuery } from "@tanstack/react-query";
import { Option } from "@mui/joy";
import locationsApi from "../../service/locationsApi";

type LessonFromProps = {
  withSubmit: () => void;
  calendar: UpdateCalendarRequest;
};

const EditCalendarForm = (props: LessonFromProps) => {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    control,
  } = useForm<UpdateCalendarRequest>({
    resolver: zodResolver(newCalenarSchema),
    defaultValues: {
      id: props.calendar.id,
      name: props.calendar.name,
      thumbnail: props.calendar.thumbnail,
      locationId: props.calendar.locationId,
    },
  });

  const {
    data: locations,
    isLoading: isLoadingLocations,
    isError: isErrorLocations,
  } = useQuery({
    queryKey: ["locations"],
    queryFn: () => locationsApi.getAll(),
  });

  const { mutate: editLesson } = useMutation(calendarsApi.update, {
    onSuccess: () => {
      queryClient.invalidateQueries(["calendars"]);
      toast.success("Kalendář byl upraven");
    },
    onError: (error) => {
      import.meta.env.DEBUG && console.log(error);
      toast.error("Nepodařilo se vytvořit kalendář");
    },
  });

  const onSubmit = (data: UpdateCalendarRequest) => {
    if (data.thumbnail.startsWith("data:image/")) {
      data.thumbnail = data.thumbnail.substring(
        data.thumbnail.indexOf(",") + 1
      );
    }
    editLesson({
      id: props.calendar.id,
      name: data.name,
      locationId: data.locationId,
      thumbnail: data.thumbnail,
    });
    reset();
    props.withSubmit();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Grid container spacing={2}>
        <Grid xs={12}>
          <FormControl error={!!errors.name}>
            <FormLabel>Název</FormLabel>
            <Input
              required
              type="text"
              placeholder="Tenisový kurt"
              error={!!errors.name}
              {...register("name", { required: true })}
            />
          </FormControl>
          <FormHelperText>{errors.name?.message}</FormHelperText>
        </Grid>
        <Grid xs={12}>
          <FormControl error={!!errors.locationId}>
            <FormLabel>Lokace</FormLabel>
            <Controller
              name="locationId"
              control={control}
              defaultValue={props.calendar.locationId}
              render={({ field: { onChange } }) => (
                <Select
                  // value={value}
                  required
                  size="sm"
                  placeholder="Kalendář událostí"
                  defaultValue={props.calendar.locationId}
                  sx={{
                    minWidth: 200,
                  }}
                  slotProps={{ button: { sx: { whiteSpace: "nowrap" } } }}
                  onChange={(_event, value) => onChange(value)}
                >
                  {isLoadingLocations ? (
                    <Option value="all">Načítání...</Option>
                  ) : isErrorLocations ? (
                    <Option value="all">Chyba</Option>
                  ) : (
                    locations.map((location: Location) => (
                      <Option
                        key={location.id}
                        value={location.id}
                        label={location.name}
                      >
                        {location.name}
                      </Option>
                    ))
                  )}
                </Select>
              )}
            />
          </FormControl>
          <FormHelperText>{errors.locationId?.message}</FormHelperText>
        </Grid>
        <Grid xs={12}>
          <Controller
            name="thumbnail" // Name should match your form data field
            control={control}
            defaultValue={props.calendar.thumbnail}
            render={({ field }) => (
              <ImageUploader
                field={{ ...field }}
                defaultImage={props.calendar.thumbnail}
              />
            )}
          />
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
          onClick={() => import.meta.env.DEBUG && console.log(errors)}
        >
          Uložit
        </Button>
      </Box>
    </form>
  );
};

export default EditCalendarForm;
