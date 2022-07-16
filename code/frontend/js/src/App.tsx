
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
import { createRepository, EMAIL_KEY, LoggedInContext, NAME_KEY, SESSION_KEY } from './user/Session';
import { Logout } from './user/logout';
import { ListDevices } from './devices/ListDevices';
import { DeviceRep } from './devices/Device';
import { TicketRequest } from './ticket/report/TickerRequest';
import { ListCategories } from './category/ListCategories';

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
                <Route path="/qrcode/:hash" element= {<TicketRequest/>}/>
                <Route path="/persons/:personId" element= {<Profile/>}/>
                <Route path="/tickets/" element= {<ListTickets/>}/>
                <Route path="/employees" element= {<ListEmployees/>}/>
                <Route path="/persons" element= {<ListPersons/>}/>
                <Route path="/devices" element= {<ListDevices/>}/>
                <Route path="/devices/:deviceId" element= {<DeviceRep/>}/>
                <Route path="/companies" element= {<ListCompanies/>}/>
                <Route path="/categories" element= {<ListCategories/>}/>
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
    const [userEmail, setUserEmail] = useState(sessionStorage.getItem(EMAIL_KEY))

    console.log(sessionStorage.getItem(NAME_KEY))

    useEffect(() => {
        console.log("Use effect for save values called")
        if (isLoggedIn && userName && userEmail) { 
            sessionStorage.setItem(SESSION_KEY, JSON.stringify(isLoggedIn))
            sessionStorage.setItem(NAME_KEY, userName)
            sessionStorage.setItem(EMAIL_KEY, userEmail)
        } else {
            sessionStorage.removeItem(SESSION_KEY)
            sessionStorage.removeItem(NAME_KEY)
            sessionStorage.removeItem(EMAIL_KEY)
        }
    }, [isLoggedIn])

    const currentSessionContext = { 
        isLoggedIn: isLoggedIn,
        userName: userName,
        userRole: userEmail,
        login: (username: string, password: string) => {
            userSessionRepo.login(username, password).then(async response => {
                let areCredentialsValid = (response.status === 200)
                if (areCredentialsValid) {
                    console.log(response)
                    setLoggedIn(true)
                    //let userInfo = await response.json()
                    setUserName('joao arcanjo')
                    setUserEmail('joni@isel.com')
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
    <div className="space-y-4">
        <LoggedInContext.Provider value={currentSessionContext}>
            <AppRouter/>
            <div></div>
        </LoggedInContext.Provider>
    </div>
  )
}

export default App;