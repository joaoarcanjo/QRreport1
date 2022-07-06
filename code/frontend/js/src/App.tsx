
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

import './App.css';
import NavBar from './components/navbar/NavBar';
import { Home } from './Home';
import LoginForm from './user/login/LoginForm';
import SignupForm from './user/signup/SignupForm';
import FormValidator from './ticket/TicketValidator';
import { useEffect, useState } from 'react';
import { SESSION_KEY, NAME_KEY, ROLE_KEY, createRepository, LoggedInContext } from './user/Session';
import { Profile } from './user/Profile';
import { ListTickets } from './ticket/ListTickets';
import { UpdateProfile } from './user/UpdateProfile';
import { ManagerTickets } from './ticket/ManagerTickets';
import { DeliverTicket } from './ticket/DeliverTicket';
import { ListPersons } from './user/ListUsers';
import { ListEmployees } from './user/ListEmployees';
import { TicketRep } from './ticket/Ticket';
import { ListCompanies } from './company/ListCompanies';
import { CompanyRep } from './company/Company';
import { BuildingRep } from './building/Building';
import { RoomRep } from './room/Room';
import { UpdateCompany } from './company/UpdateCompany';
import { UpdateBuilding } from './building/UpdateBuilding';

const userSessionRepo = createRepository()

function AppRouter() {
    return (
        <Router>
            <NavBar/>
            <Routes>
                <Route path="/" element= {<Home/>}/>
                <Route path="/login" element= {<LoginForm/>}/>
                <Route path="/signup" element= {<SignupForm/>}/>
                <Route path="/qrcode/:hash" element= {<FormValidator/>}/>
                <Route path="/profile" element= {<Profile/>}/>
                <Route path="/updateProfile" element= {<UpdateProfile/>}/>
                <Route path="/tickets" element= {<ListTickets/>}/>
                <Route path="/managerTickets" element= {<ManagerTickets/>}/>
                <Route path="/deliveTicket/:id" element= {<DeliverTicket/>}/>
                <Route path="/employees" element= {<ListEmployees/>}/>
                <Route path="/persons" element= {<ListPersons/>}/>
                <Route path="/companies" element= {<ListCompanies/>}/>
                <Route path="/companies/:id" element= {<CompanyRep/>}/>
                <Route path="/updatecompany/:id" element= {<UpdateCompany/>}/>
                <Route path="/buildings/:id" element= {<BuildingRep/>}/>
                <Route path="/updatebuilding/:id" element= {<UpdateBuilding/>}/>
                <Route path="/rooms/:id" element= {<RoomRep/>}/>
                <Route path="/tickets/:id" element= {<TicketRep/>}/>
            </Routes>
        </Router>
    )
}


function App() {

    const [isLoggedIn, setLoggedIn] = useState(sessionStorage.getItem(SESSION_KEY) === "true")
    const [userName, setUserName] = useState(sessionStorage.getItem(NAME_KEY))
    const [userRole, setUserRole] = useState(sessionStorage.getItem(ROLE_KEY))

    useEffect(() => {
        console.log("Use effect for save values called")
        if (isLoggedIn && userName && userRole) { 
            sessionStorage.setItem(SESSION_KEY, JSON.stringify(isLoggedIn))
            sessionStorage.setItem(NAME_KEY, userName)
            sessionStorage.setItem(ROLE_KEY, userRole)
        } else {
            sessionStorage.removeItem(SESSION_KEY)
            sessionStorage.removeItem(NAME_KEY)
            sessionStorage.removeItem(ROLE_KEY)
        }
    }, [isLoggedIn])

    const currentSessionContext = { 
        isLoggedIn: isLoggedIn,
        userName: userName,
        userRole: userRole,
        login: (username: string, password: string) => {
            userSessionRepo.login(username, password).then(async response => {
                ///let validCredentials = (response.status === 200)
                let validCredentials = true
                if (validCredentials) {
                    setLoggedIn(validCredentials)
                    ///let userInfo = await response.json()
                    let userInfo = {name: 'Joao', role: 'user'}
                    setUserName(userInfo.name)
                    setUserRole(userInfo.role)
                }
            })
        },
        logout: () => {
            userSessionRepo.logout().then(status => {
                /*if(status === 200) {
                    setLoggedIn(false)
                }*/
                setLoggedIn(false)
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