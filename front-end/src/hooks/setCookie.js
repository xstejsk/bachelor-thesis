import Cookie from "js-cookie";

const SetCookie = (cookiename, usrin) => {
  Cookie.set(cookiename, usrin, {
    path: "/",
  });
};
export default SetCookie;
