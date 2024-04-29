import { useState } from "react";
import * as FaIcons from "react-icons/fa";
import * as AiIcons from "react-icons/ai";
import * as BsIcons from "react-icons/bs";
import { Link } from "react-router-dom";
import { Agendas, NavbarData } from "./NavbarData";
import "./Navbar.css";
import { IconContext } from "react-icons";
import { INavbarData } from "./NavbarData";
import Box from "@mui/joy/Box";
import Typography from "@mui/joy/Typography";
import { IconButton, Tooltip } from "@mui/joy";
import { useRecoilState } from "recoil";
import authAtom from "../state/authAtom";
import { Role } from "../models/user";
import { useLogout } from "../hooks/useLogout";
import useUsersBalance from "../hooks/useUsersBalance";

function Navbar() {
  const [sidebar, setSidebar] = useState(false);
  const { currentBalance } = useUsersBalance();
  const { logout } = useLogout();

  const showSidebar = () => setSidebar(!sidebar);

  const [auth] = useRecoilState(authAtom);

  return (
    <>
      <IconContext.Provider value={{ color: "#fff" }}>
        <Box
          className="navbar"
          sx={{
            backgroundColor: "primary.solidBg",
            display: "flex",
            height: "56px",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <IconButton
            className="menu-bars"
            onClick={showSidebar}
            sx={{
              "&:hover": {
                backgroundColor: "primary.solidBg", // Change the background color on hover
              },
            }}
          >
            <FaIcons.FaBars />
          </IconButton>
          {auth.user.role !== Role.GUEST && (
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                alignItems: "center",
                justifyContent: "center",
                gap: "1rem",
              }}
            >
              <Typography
                level="title-lg"
                sx={{
                  color: "white",
                }}
                startDecorator={
                  auth.user.role === Role.ADMIN ? (
                    <FaIcons.FaUserGraduate />
                  ) : (
                    <FaIcons.FaUser />
                  )
                }
              >
                {auth.user.firstName}
              </Typography>
              <Tooltip title="Zůstatek na účtu">
                <Typography
                  level="title-lg"
                  sx={{
                    color: "white",
                    marginRight: "2rem",
                  }}
                  startDecorator={<FaIcons.FaCoins />}
                  endDecorator={"Kr."}
                >
                  {currentBalance}
                </Typography>
              </Tooltip>
            </Box>
          )}
        </Box>

        <Box
          onClick={() => setSidebar(false)}
          sx={{
            position: "fixed",
            top: "0",
            left: "0",
            width: "100vw",
            height: "100vh",
            backgroundColor: "rgba(0,0,0,0.4)",
            display: sidebar ? "block" : "none",
            zIndex: 9998,
          }}
        ></Box>
        <Box
          sx={{
            zIndex: 9999,
          }}
        >
          <nav className={sidebar ? "nav-menu active" : "nav-menu"}>
            <ul className="nav-menu-items">
              <li className="navbar-toggle">
                <Typography level="title-md" sx={{ width: "100%" }}>
                  <IconButton
                    className="menu-bars"
                    onClick={showSidebar}
                    sx={{
                      "&:hover": {
                        backgroundColor: "primary.solidActiveBg", // Change the background color on hover
                      },
                    }}
                  >
                    <AiIcons.AiOutlineClose />
                  </IconButton>
                </Typography>
              </li>
              {NavbarData.filter((item: INavbarData) =>
                item.roles.includes(auth.user.role)
              ).map((item: INavbarData) => {
                return (
                  <li
                    key={item.title}
                    className={item.cName}
                    onClick={showSidebar}
                  >
                    <Typography
                      level="title-md"
                      sx={{ width: "100%", height: "100%" }}
                    >
                      <Link to={item.path}>
                        {item.icon}
                        <Typography sx={{ marginLeft: "16px" }}>
                          {item.title}
                        </Typography>
                      </Link>
                    </Typography>
                  </li>
                );
              })}
              {auth.user.role === Role.ADMIN && (
                <li className="nav-text">
                  <Typography
                    level="title-md"
                    sx={{ width: "100%", height: "100%" }}
                  >
                    <Typography>
                      {/* {item.icon} */}
                      <Typography sx={{ marginLeft: "16px" }}>
                        Agendy
                      </Typography>
                    </Typography>
                  </Typography>
                </li>
              )}
              {auth.user.role === Role.ADMIN &&
                Agendas.map((item: INavbarData) => {
                  return (
                    <li
                      key={`agenda-${item.title}`}
                      className={item.cName}
                      onClick={showSidebar}
                    >
                      <Typography
                        level="title-md"
                        sx={{ width: "100%", height: "100%" }}
                      >
                        <Link to={`${item.path}?page=0&size=10`}>
                          {item.icon}
                          <Typography sx={{ marginLeft: "16px" }}>
                            {item.title}
                          </Typography>
                        </Link>
                      </Typography>
                    </li>
                  );
                })}
            </ul>

            <button
              className="nav-menu_logout-button"
              type="button"
              onClick={() => {
                logout();
                showSidebar();
              }}
            >
              <Typography
                level="title-md"
                sx={{ width: "100%", height: "100%" }}
                component={"div"}
              >
                <Link to="/login">
                  <Typography
                    startDecorator={
                      auth.user.role !== Role.GUEST ? (
                        <BsIcons.BsBoxArrowLeft />
                      ) : (
                        <BsIcons.BsBoxArrowInRight />
                      )
                    }
                    component={"div"}
                    sx={{
                      color: "white",
                      textDecoration: "none",
                      width: "100%",
                      height: "100%",
                      display: "flex",
                      justifyContent: "center",
                    }}
                  >
                    {auth.user.role !== Role.GUEST
                      ? "Odhlásit se"
                      : "Přihlásit se"}
                  </Typography>
                </Link>
              </Typography>
            </button>
          </nav>
        </Box>
      </IconContext.Provider>
    </>
  );
}

export default Navbar;
