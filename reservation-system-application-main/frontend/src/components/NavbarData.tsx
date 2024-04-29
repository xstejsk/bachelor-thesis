import * as AiIcons from "react-icons/ai";
import * as BsIcons from "react-icons/bs";
import { Role } from "../models/user";

export interface INavbarData {
  title: string;
  path: string;
  icon: JSX.Element;
  cName: string;
  roles: Role[];
}

export const Agendas: INavbarData[] = [
  {
    title: "Rezervace",
    path: "/agendas/reservations",
    icon: <AiIcons.AiFillBook />,
    cName: "nav-text",
    roles: [Role.ADMIN],
  },
  {
    title: "Uživatelé",
    path: "/agendas/users",
    icon: <BsIcons.BsFillPersonFill />,
    cName: "nav-text",
    roles: [Role.ADMIN],
  },
  {
    title: "Události",
    path: "/agendas/events",
    icon: <BsIcons.BsCalendarEventFill />,
    cName: "nav-text",
    roles: [Role.ADMIN],
  },
  {
    title: "Kalendáře",
    path: "/agendas/calendars",
    icon: <AiIcons.AiFillCalendar />,
    cName: "nav-text",
    roles: [Role.ADMIN],
  },
];

export const NavbarData: INavbarData[] = [
  {
    title: "Kalendáře",
    path: "/calendars",
    icon: <AiIcons.AiFillCalendar />,
    cName: "nav-text",
    roles: [Role.GUEST, Role.USER, Role.ADMIN],
  },
  {
    title: "Mé rezervace",
    path: "/reservations?page=0&size=10",
    icon: <AiIcons.AiFillBook />,
    cName: "nav-text",
    roles: [Role.USER, Role.ADMIN],
  },
  {
    title: "Účet",
    path: "/account",
    icon: <BsIcons.BsFillPersonFill />,
    cName: "nav-text",
    roles: [Role.USER, Role.ADMIN],
  },
];
