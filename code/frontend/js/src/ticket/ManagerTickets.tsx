import { useState } from "react"
import { Link } from "react-router-dom"
import { MdFilterList } from "react-icons/md";
import { TbArrowBigTop, TbArrowBigDown } from "react-icons/tb";
import { TicketItem } from "../Types";

export function ManagerTickets() {

    //with false, direction is desc, else is asc
    const [direction, setDirections] = useState(false)

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
        return (
            <div className="list-none">
                <div className="p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100 divide-y space-y-4">  
                    <div>
                        <Link to={`/deliveTicket/${ticket.id}`}>
                            <h5 className="mb-2 text-xl font-md tracking-tight text-gray-900">{ticket.subject}</h5>
                        </Link>
                    </div>
                </div>
            </div>
        )
    }

    function Tickets({tickets}: {tickets: TicketItem[]}) {
        return <> {tickets.map(ticket => <TicketItemComponent ticket= {ticket}/>)} </>
    }

    const ticketsMocks = [
        {'id': 1, 'subject': 'Torneira avariada', 'employeeState': 'EmState', 'userState': 'UserState'},
        {'id': 2, 'subject': 'Torneira suja', 'employeeState': 'EmState', 'userState': 'UserState'},
        {'id': 3, 'subject': 'Parede mal cheirosa e cheia de bolor que nojo', 'employeeState': 'EmState', 'userState': 'UserState'},
        {'id': 4, 'subject': 'Chão partido', 'employeeState': 'EmState', 'userState': 'UserState'},
        {'id': 5, 'subject': 'Cadeira partida', 'employeeState': 'EmState', 'userState': 'UserState'}
    ]

    return (
        <div>
            <div className='px-3 pt-3 space-y-4'>
                <Filters/>
                <Tickets tickets={ticketsMocks}/>
            </div>
        </div>
    )
}