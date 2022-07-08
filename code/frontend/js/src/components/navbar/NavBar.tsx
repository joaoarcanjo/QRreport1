import { NavLink } from "react-router-dom";
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
            <h3>QRreport</h3>
            <nav ref={navRef}>
                <NavElement navTo='/' text='Home'/>
                {!userSession?.isLoggedIn && <NavElement navTo='login' text='Login'/>}
                {userSession?.isLoggedIn && <NavElement navTo={`persons/${'4b341de0-65c0-4526-8898-24de463fc315'}`} text='My profile'/>}
                <NavElement navTo='managerTickets' text='Manager tickets'/>
                <NavElement navTo='persons' text='Persons'/>
                <NavElement navTo='employees' text='Employees'/>
                <NavElement navTo='companies' text='Companies'/>
                <button className='nav-btn nav-close-btn'>
                    <FaTimes onClick={showNavbar}/>
                </button>
            </nav>
            <button className='nav-btn'>
                <FaBars onClick={showNavbar}/>
            </button>
        </header>
    )
}