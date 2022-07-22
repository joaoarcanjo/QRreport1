
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'

import './App.css';
import NavBar from './components/navbar/NavBar';
import { Home } from './Home';
import LoginForm from './user/login/LoginForm';
import SignupForm from './user/signup/SignupForm';
import { useEffect, useState } from 'react';
import { Profile } from './user/profile/Profile';
import { ListTickets } from './ticket/ListTickets';
import { ListPersons } from './user/ListUsers';
import { TicketRep } from './ticket/Ticket';
import { ListCompanies } from './company/ListCompanies';
import { CompanyRep } from './company/Company';
import { BuildingRep } from './building/Building';
import { RoomRep } from './room/Room';
import { ACTIVE_ROLE, createRepository, EMAIL_KEY, LoggedInContext, NAME_KEY, SESSION_KEY, useLoggedInState } from './user/Session';
import { Logout } from './user/logout';
import { ListDevices } from './devices/ListDevices';
import { DeviceRep } from './devices/Device';
import { TicketRequest } from './ticket/report/TickerRequest';
import { ListCategories } from './category/ListCategories';
import { ErrorView } from './errors/Error';
import { mapToFetchResult } from './hooks/useFetch';
import { getEntityOrUndefined, getProblemOrUndefined } from './models/ModelUtils';
import { LoginUser } from './models/Models';
import { ProblemJson } from './models/ProblemJson';
import { ErrorPopup } from './components/ErrorPopup';

const userSessionRepo = createRepository()

function AppRouter() {
    const userSession = useLoggedInState()
    const isAuthenticated = userSession?.isLoggedIn
    return (
        <Router>
            <NavBar/>
            <Routes>
                {/* <Route path="/" element= {<Home/>}/> */}
                <Route path="/report/:hash" element= {<TicketRequest/>}/>
                {!isAuthenticated ? 
                    <Route path="*" element={<Navigate to="/login"/>}/> :
                <>
                    <Route path="/logout" element= {<Logout/>}/>
                    <Route path="/profile" element= {<Profile/>}/>
                    <Route path="/tickets/" element= {<ListTickets/>}/>
                    <Route path="/tickets/:ticketId" element= {<TicketRep/>}/>
                    <Route path="/persons" element= {<ListPersons/>}/>
                    <Route path="/persons/:personId" element= {<Profile/>}/>
                    <Route path="/devices" element= {<ListDevices/>}/>
                    <Route path="/devices/:deviceId" element= {<DeviceRep/>}/>
                    <Route path="/companies" element= {<ListCompanies/>}/>
                    <Route path="/categories" element= {<ListCategories/>}/>
                    <Route path="/companies/:companyId" element= {<CompanyRep/>}/>
                    <Route path="/companies/:companyId/buildings/:buildingId" element= {<BuildingRep/>}/>
                    <Route path="/companies/:companyId/buildings/:buildingId/rooms/:roomId" element= {<RoomRep/>}/>
                    <Route path="*" element={<ErrorView message="Page not found"/>}/>
                </>}
                <Route path="/login" element= {isAuthenticated ? <Navigate to="/profile"/> : <LoginForm/>}/> 
                <Route path="/signup" element= {isAuthenticated ? <Navigate to="/profile"/> : <SignupForm/>}/>                        
            </Routes>
        </Router>
    )
}

function App() {

    const [isLoggedIn, setLoggedIn] = useState(sessionStorage.getItem(SESSION_KEY) === "true")
    const [activeRole, setActiveRole] = useState(sessionStorage.getItem(ACTIVE_ROLE))
    const [userEmail, setUserEmail] = useState(sessionStorage.getItem(EMAIL_KEY))
    const [userName, setUserName] = useState(sessionStorage.getItem(NAME_KEY))
    const [problem, setProblem] = useState<ProblemJson | undefined>()

    useEffect(() => {
        console.log(isLoggedIn, activeRole, userEmail, userName)
        if (isLoggedIn && userName && userEmail && activeRole) { 
            sessionStorage.setItem(SESSION_KEY, JSON.stringify(isLoggedIn))
            sessionStorage.setItem(NAME_KEY, userName)
            sessionStorage.setItem(EMAIL_KEY, userEmail)
            sessionStorage.setItem(ACTIVE_ROLE, activeRole)
        } else {
            sessionStorage.removeItem(ACTIVE_ROLE)
            sessionStorage.removeItem(SESSION_KEY)
            sessionStorage.removeItem(NAME_KEY)
            sessionStorage.removeItem(EMAIL_KEY)
        }
    }, [isLoggedIn, activeRole])

    function login(username: string, password: string) {
        userSessionRepo.login(username, password).then(async response => {
            const payload = await response.json()
            const header = response.headers.get('content-type')
            const fetchResult = mapToFetchResult<LoginUser>(payload, header)
            const entity = getEntityOrUndefined(fetchResult)
            const problem = getProblemOrUndefined(fetchResult)
            
            if (problem) {
                setProblem(problem)
                return
            } 
            setProblem(undefined)
            if (entity) {
                setLoggedIn(true)
                setUserName(entity.properties.name)
                setUserEmail(entity.properties.email)
                setActiveRole(entity.properties.activeRole)
            }
        })
    }

    function logout() {
        userSessionRepo.logout().then(response => {
            if(response === 200) {
                setLoggedIn(false)
            }
        })
    }

    function signup(name: string, phone: string, email: string, password: string, passwordVerify: string) {
        userSessionRepo.signup(name, phone, email, password, passwordVerify).then(async response => {
            const payload = await response.json()
            const header = response.headers.get('content-type')
            const fetchResult = mapToFetchResult<LoginUser>(payload, header)
            const entity = getEntityOrUndefined(fetchResult)
        
            if (entity) {
                setLoggedIn(true)
                setUserName(entity.properties.name)
                setUserEmail(entity.properties.email)
                setActiveRole(entity.properties.activeRole)
            }
        })
    }

    function changeRole(role: string) {
        setActiveRole(role)
    }

    const currentSessionContext = { 
        problem: problem,
        isLoggedIn: isLoggedIn,
        userName: userName,
        userRole: activeRole,
        login: login,
        logout: logout,
        signup: signup,
        changeRole: changeRole
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