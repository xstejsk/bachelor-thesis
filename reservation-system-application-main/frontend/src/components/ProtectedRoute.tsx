import React from "react";
import { Navigate, Outlet } from "react-router";
import { useRecoilValue } from "recoil";
import { Role } from "../models/user";
import authAtom from "../state/authAtom";

interface ProtectedRouteProps {
  children?: React.ReactElement;
  requiredRoles: Role[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRoles,
}) => {
  const auth = useRecoilValue(authAtom);
  if (!auth || auth.user.role === Role.GUEST) {
    return <Navigate to="/sign-in" replace />;
  }

  if (!requiredRoles.includes(auth.user.role)) {
    return <Navigate to="/calendars" replace />;
  }

  return children ? children : <Outlet />;
};

export default ProtectedRoute;
