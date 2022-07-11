import { Navigate } from "react-router-dom";
import { useLoggedInState } from "./Session";

export function Logout() {
    const userSession = useLoggedInState()
    userSession?.logout()
    return <Navigate to={'/'}/>
}