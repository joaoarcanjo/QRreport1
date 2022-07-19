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

    function NavElements() {
        return (!userSession?.isLoggedIn) ?
             <NavElement navTo='login' text='Login'/> :
            (<>
                <NavElement navTo='profile' text='My profile'/>
                <NavElement navTo='persons' text='Persons'/>
                <NavElement navTo='tickets' text='Tickets'/>
                <NavElement navTo='companies' text='Companies'/>
                <NavElement navTo='categories' text='Categories'/>
                <NavElement navTo='devices' text='Devices'/>
                <NavElement navTo='logout' text='Logout'/>
            </>)
    }

    return (
        <header>
            <NavLink to='/'>
                <h3>QRreport</h3>
            </NavLink>
            <nav ref={navRef} className="flex items-center">
                <NavElements/>
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