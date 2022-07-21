import { Navigate } from "react-router-dom";
import { HOME_URL, LOGIN_URL } from "../Urls";
import { useLoggedInState } from "./Session";

export function Logout() {
    const userSession = useLoggedInState()

    if(userSession?.isLoggedIn) {
        userSession?.logout()
        return <Navigate to={HOME_URL}/>
    }
    return <Navigate to={LOGIN_URL}/>
}