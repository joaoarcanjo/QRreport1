
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

import './App.css';
import NavBar from './components/navbar/NavBar';
import { Home } from './Home';
import LoginForm from './user/login/LoginForm';
import SignupForm from './user/signup/SignupForm';
import { useEffect, useState } from 'react';
import { Profile } from './user/profile/Profile';
import { ListTickets } from './ticket/ListTickets';
import { ListPersons } from './user/ListUsers';
import { ListEmployees } from './user/ListEmployees';
import { TicketRep } from './ticket/Ticket';
import { ListCompanies } from './company/ListCompanies';
import { CompanyRep } from './company/Company';
import { BuildingRep } from './building/Building';
import { RoomRep } from './room/Room';
import { createRepository, LoggedInContext, NAME_KEY, ROLE_KEY, SESSION_KEY } from './user/Session';
import { Logout } from './user/logout';
import { ListDevices } from './devices/ListDevices';
import { DeviceRep } from './devices/Device';
import FormTicket from './ticket/report/TicketForm';

const userSessionRepo = createRepository()

function AppRouter() {
    return (
        <Router>
            <NavBar/>
            <Routes>
                <Route path="/" element= {<Home/>}/>
                <Route path="/login" element= {<LoginForm/>}/>
                <Route path="/logout" element= {<Logout/>}/>
                <Route path="/signup" element= {<SignupForm/>}/>
                <Route path="/qrcode/:hash" element= {<FormTicket/>}/>
                <Route path="/persons/:personId" element= {<Profile/>}/>
                <Route path="/tickets/" element= {<ListTickets/>}/>
                <Route path="/employees" element= {<ListEmployees/>}/>
                <Route path="/persons" element= {<ListPersons/>}/>
                <Route path="/devices" element= {<ListDevices/>}/>
                <Route path="/devices/:deviceId" element= {<DeviceRep/>}/>
                <Route path="/companies" element= {<ListCompanies/>}/>
                <Route path="/companies/:companyId" element= {<CompanyRep/>}/>
                <Route path="/companies/:companyId/buildings/:buildingId" element= {<BuildingRep/>}/>
                <Route path="/companies/:companyId/buildings/:buildingId/rooms/:roomId" element= {<RoomRep/>}/>
                <Route path="/tickets/:ticketId" element= {<TicketRep/>}/>
            </Routes>
        </Router>
    )
}


function App() {

    const [isLoggedIn, setLoggedIn] = useState(sessionStorage.getItem(SESSION_KEY) === "true")
    const [userName, setUserName] = useState(sessionStorage.getItem(NAME_KEY))
    const [userRole, setUserRole] = useState(sessionStorage.getItem(ROLE_KEY))

    console.log(isLoggedIn)

    useEffect(() => {
        console.log("Use effect for save values called")
        if (isLoggedIn /*&& userName && userRole*/) { 
            sessionStorage.setItem(SESSION_KEY, JSON.stringify(isLoggedIn))
            //sessionStorage.setItem(NAME_KEY, userName)
            //sessionStorage.setItem(ROLE_KEY, userRole)
        } else {
            sessionStorage.removeItem(SESSION_KEY)
            //sessionStorage.removeItem(NAME_KEY)
            //sessionStorage.removeItem(ROLE_KEY)
        }
    }, [isLoggedIn])

    const currentSessionContext = { 
        isLoggedIn: isLoggedIn,
        userName: userName,
        userRole: userRole,
        login: (username: string, password: string) => {
            userSessionRepo.login(username, password).then(response => {
                let areCredentialsValid = (response.status === 200)
                if (areCredentialsValid) {
                    setLoggedIn(areCredentialsValid)
                    //let userInfo = await response.json()
                    //setUserName(userInfo.name)
                    //setUserRole(userInfo.role)
                }
            })
        },
        logout: () => {
            userSessionRepo.logout().then(response => {
                if(response === 200) {
                    setLoggedIn(false)
                }
            })
        }
}

return (
    <div>
        <LoggedInContext.Provider value={currentSessionContext}>
            <AppRouter/>
        </LoggedInContext.Provider>
    </div>
  )
}

export default App;