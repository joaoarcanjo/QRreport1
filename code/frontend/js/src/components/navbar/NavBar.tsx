import { Link, NavLink } from "react-router-dom";
//import { useLoggedInState } from "../authentication/Session
import { useRef } from "react";
import { FaBars, FaTimes } from "react-icons/fa";
import "./Style.css"
import { useLoggedInState } from "../../user/Session";

export default function NavBar() {

    const navRef = useRef<HTMLDivElement>(null)

    const userSession = useLoggedInState()

    const showNavbar = () => {
        navRef.current?.classList.toggle("responsive_nav")
    }

    type AProps = { navTo: string, text: string }

    const NavElement = ({navTo, text}: AProps) => {
        return <span onClick={showNavbar}><NavLink to={navTo}>{text}</NavLink></span>
    }

    return (
        <header>
            <>
                <NavLink to='/'>
                    <h3>QRreport</h3>
                </NavLink>
                <nav ref={navRef} className="flex items-center">
                    {!userSession?.isLoggedIn && <NavElement navTo='login' text='Login'/>}
                    {userSession?.isLoggedIn && <NavElement navTo={`persons/${'4b341de0-65c0-4526-8898-24de463fc315'}`} text='My profile'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='managerTickets' text='Manager tickets'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='persons' text='Persons'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='employees' text='Employees'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='companies' text='Companies'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='categories' text='Categories'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='devices' text='Devices'/>}
                    {userSession?.isLoggedIn && <NavElement navTo='logout' text='Logout'/>}
                    <button className='nav-btn nav-close-btn'>
                        <FaTimes onClick={showNavbar}/>
                    </button>
                </nav>
                <button className='nav-btn'>
                    <FaBars onClick={showNavbar}/>
                </button>
            </>
        </header>
    )
}