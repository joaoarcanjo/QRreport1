import { NavLink } from "react-router-dom";
//import { useLoggedInState } from "../authentication/Session
import { useRef } from "react";
import { FaBars, FaTimes } from "react-icons/fa";
import "./Style.css"
import { ADMIN_ROLE, MANAGER_ROLE, useLoggedInState } from "../../user/Session";

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

        let components = []

        if(!userSession?.isLoggedIn) {
            components.push(<NavElement key={'login'} navTo='login' text='Login'/>)
            components.push(<NavElement key={'signup'} navTo='signup' text='Signup'/>)
        } else {
            if(userSession.userRole === MANAGER_ROLE || userSession.userRole === ADMIN_ROLE) {
                components.push(<NavElement key={'companies'} navTo='companies' text='Companies'/>)
            }
            
            if(userSession.userRole === ADMIN_ROLE) {
                components.push(<NavElement key={'persons'} navTo='persons' text='Persons'/>)
                components.push(<NavElement key={'categories'} navTo='categories' text='Categories'/>)
                components.push(<NavElement key={'devices'} navTo='devices' text='Devices'/>)
            }     

            components.push(<NavElement key={'tickets'} navTo='tickets' text='Tickets'/>)
            components.push(<NavElement key={'profile'} navTo='profile' text='My profile'/>)
            components.push(<NavElement key={'logout'} navTo='logout' text='Logout'/>)
        }
        return <>{components}</>
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