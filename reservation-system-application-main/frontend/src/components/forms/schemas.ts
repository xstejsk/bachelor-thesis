import { z } from "zod";
import { startOfDay } from "date-fns";

export const newUserSchema = z
  .object({
    email: z.string().email(),
    firstName: z.string(),
    lastName: z.string(),
    password: z.string(),
    confirmPassword: z.string(),
    hasDailyDiscount: z.boolean(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Hesla se neshodují",
    path: ["confirmPassword"],
  })
  .refine((data) => data.password.length >= 8, {
    message: "Heslo musí mít minimálně 8 znaků",
    path: ["password"],
  });

export const changePasswordSchema = z
  .object({
    oldPassword: z.string(),
    newPassword: z.string().min(8),
    confirmPassword: z.string().min(8),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Hesla se neshodují",
    path: ["confirmPassword"],
  })
  .refine((data) => data.newPassword.length >= 8, {
    message: "Heslo musí mít minimálně 8 znaků",
    path: ["password"],
  });

export const credentialsSchema = z.object({
  username: z.string(),
  password: z.string(),
});

export const newCalenarSchema = z
  .object({
    name: z.string(),
    thumbnail: z.string(),
    locationId: z.string(),
  })
  .refine((data) => data.name.length <= 250, {
    message: "Název může mít maximálně 250 znaků",
    path: ["name"],
  });

const recurrenceSchema = z
  .object({
    repeatUntil: z.string(),
    daysOfWeek: z.array(z.string()),
  })
  .refine(
    (data) => {
      if (data.repeatUntil) {
        return startOfDay(new Date(data.repeatUntil)) >= startOfDay(new Date());
      }
      return true;
    },
    { message: "Datum konce musí být v budoucnosti", path: ["repeatUntil"] }
  );

export const newEventSchema = z
  .object({
    title: z.string(),
    description: z.string(),
    date: z.string(),
    startTime: z.string(),
    endTime: z.string(),
    price: z.number(),
    maximumCapacity: z.number(),
    recurrence: recurrenceSchema,
    discountPrice: z.number(),
  })
  .refine((data) => data.startTime < data.endTime, {
    message: "Začátek musí být před koncem",
    path: ["startTime"],
  })
  .refine((data) => new Date(data.date) > new Date(), {
    message: "Nelze vytvořit událost v minulosti",
    path: ["date"],
  })
  .refine((data) => data.description.length <= 250, {
    message: "Popis může mít maximálně 250 znaků",
    path: ["description"],
  })
  .refine((data) => data.title.length <= 250, {
    message: "Název může mít maximálně 250 znaků",
    path: ["title"],
  })
  .refine((data) => data.maximumCapacity > 0, {
    message: "Kapacita musí být větší než 0",
    path: ["maximumCapacity"],
  })
  .refine((data) => data.price >= 0, {
    message: "Cena musí být větší nebo rovna 0",
    path: ["price"],
  })
  .refine((data) => data.discountPrice >= 0, {
    message: "Cena musí být větší nebo rovna 0",
    path: ["discountPrice"],
  })
  .refine((data) => data.discountPrice <= data.price, {
    message: "Slevová cena musí být menší nebo rovna ceně",
    path: ["discountPrice"],
  })
  .refine(
    (data) => {
      if (data.recurrence.repeatUntil) {
        return new Date(data.recurrence.repeatUntil) >= new Date(data.date);
      }
      return true;
    },
    {
      message: "Datum konce musí být větší nebo rovno datu začátku",
      path: ["recurrence", "repeatUntil"],
    }
  );

export const updateEventSchema = z
  .object({
    title: z.string(),
    description: z.string(),
    updateSeries: z.boolean(),
  })
  .refine((data) => data.title.length <= 250, {
    message: "Název může mít maximálně 250 znaků",
    path: ["title"],
  })
  .refine((data) => data.description.length <= 250, {
    message: "Popis může mít maximálně 250 znaků",
    path: ["description"],
  });
