import { useState } from "react"
import { Link } from "react-router-dom"
import { MdExpandMore, MdExpandLess, MdFilterList } from "react-icons/md";
import { TbArrowBigTop, TbArrowBigDown } from "react-icons/tb";
import { useLoggedInState } from "../user/Session";
import React from "react";
import { TicketItem } from "../Types";

export function ListTickets() {

    //with false, direction is desc, else is asc
    const [direction, setDirections] = useState(false)

    React.useEffect(()=> {
        fetch("http://localhost:8080/v1/tickets")
            .then(async response => {
                console.log(await response.json())
            })
    },[])

    function Filters() {
        return (
            <div className='flex w-full gap-4'>
                <select className='border rounded-lg'>
                    <option value='date'>Date</option>
                    <option value='name'>Name</option>
                </select>        
                <button 
                    className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-5 h-12 inline-flex items-center'
                    onClick= {() => setDirections(!direction)}>
                    {direction && <TbArrowBigTop style= {{ color: 'white', fontSize: '2em' }} />}
                    {!direction && <TbArrowBigDown style= {{ color: 'white', fontSize: '2em' }} />}
                </button>     
                <button className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-5 h-12 inline-flex items-center'>
                    <MdFilterList style= {{ color: 'white', fontSize: '2em' }} /> 
                    Filter
                </button>
            </div>    
        )
    }

    function TicketItemComponent({ticket}: {ticket: TicketItem}) {

        const [moreInfo, showMoreInfo] = useState<boolean>(false)

        const desc = ticket.description === undefined ? 'No description' : ticket.description

        return (
            <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100 divide-y space-y-4'>  
                <div>
                    <Link to={`/tickets/${ticket.id}`}>
                        <h5 className='mb-2 text-xl tracking-tight text-gray-900'>{ticket.subject}</h5>
                    </Link>
                    <div className=' p-1 bg-amber-100 rounded-lg shadow-md'>
                        {useLoggedInState()?.userRole === 'user' ? ticket.userState : ticket.employeeState} 
                    </div>
                </div>
                <div>
                    <button className='my-1' onClick={() => showMoreInfo(!moreInfo)}>
                        {!moreInfo && <MdExpandMore style= {{ color: 'blue', fontSize: '2em' }} />}
                        { moreInfo && <MdExpandLess style= {{ color: 'blue', fontSize: '2em' }} />}
                    </button>
                    <div className='space-y-4'>
                        {moreInfo && <p className='text-sm text-gray-800'>{desc}</p>}
                        {moreInfo && 
                        <div className='flex justify-end'>
                            <button className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-2.5 py-1 inline-flex items-center mr-2 mb-2'> 
                                Edit
                            </button>
                        </div>}
                    </div>
                </div>
            </div>
        )
    }

    function Tickets({tickets}: {tickets: TicketItem[]}) {
        return <> {tickets.map((ticket, idx) => <TicketItemComponent key= {idx} ticket= {ticket}/>)} </>
    }

    const ticketsMocks = [
        {'id': 1, 'subject': 'Torneira avariada', 'employeeState': 'Employee State', 'userState': 'UserState'},
        {'id': 2, 'subject': 'Torneira suja', 'employeeState': 'Employee State', 'userState': 'UserState'},
        {'id': 3, 'subject': 'Parede mal cheirosa e cheia de bolor que nojo', 'employeeState': 'Employee State', 'userState': 'UserState'},
        {'id': 4, 'subject': 'Chão partido', 'employeeState': 'Employee State', 'userState': 'UserState'},
        {'id': 5, 'subject': 'Cadeira partida', 'employeeState': 'Employee State', 'userState': 'UserState'}
    ]

    return (
        <div className='px-3 pt-3 space-y-4'>
            <Filters/>
            <Tickets tickets={ticketsMocks}/>
        </div>
    )
}