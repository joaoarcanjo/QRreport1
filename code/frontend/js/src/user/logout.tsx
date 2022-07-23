import { Navigate } from "react-router-dom";
import { LOGIN_URL } from "../Urls";
import { useLoggedInState } from "./Session";

export function Logout() {
    const userSession = useLoggedInState()

    if(userSession?.isLoggedIn) {
        userSession?.logout()
    }
    return <Navigate to={LOGIN_URL}/>
}