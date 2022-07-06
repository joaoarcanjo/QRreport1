
import { useState } from "react";
import { MdFilterList } from "react-icons/md";
import { TbArrowBigTop, TbArrowBigDown } from "react-icons/tb";
import { AiFillTool, AiFillStar } from "react-icons/ai";
import { Employee, Ticket } from "../Types";

export function DeliverTicket() {

    //with false, direction is desc, else is asc
    const [direction, setDirections] = useState(false)

    const [employeeSelected, setEmployee] = useState<Employee>()

    function TicketInfo({ticket}: {ticket: Ticket}) {
        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center'>
                    <p className='text-gray-900 font-bold text-xl leading-8 my-1'>{ticket.subject}</p>
                    <div className='flex flex-col space-y-4'>
                        <p> Building {ticket.buildingName} - Room {ticket.roomName} </p>
                        <p> {ticket.category} </p>
                        <p> {ticket.description} </p>
                    </div>
                </div>
            </div>
        )
    }

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

    function EmployeeItem({employee}: {employee: Employee}) {
        return (
            <div className='flex p-5 bg-white rounded-lg border border-gray-200 shadow-md'>  
                <div className='flex space-x-4'>
                    <h5 className='text-xl font-md text-gray-900'>{employee.name}</h5>
                    <span className='flex items-center'>{employee.currentWorks}<AiFillTool style= {{ fontSize: '1.5em' }}/></span>
                    <span className='flex items-center'>{employee.avaliation}<AiFillStar style= {{ color: 'yellow', fontSize: '1.5em' }}/></span>
                    <button 
                        className='px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'
                        onClick= {() => setEmployee(employee)}>
                        Select
                    </button>
                </div>
            </div>
        )
    }

    function Employees({employees}: {employees: Employee[]}) {
        return (
            <div className='flex flex-col space-y-3'>
                {Array.from(employees).map((employee, idx) => <EmployeeItem key={idx} employee={employee}/>)}
            </div>
        )
    }

    const mockTicket = {
            'id': 1,
            'subject': 'Torneira suja',
            'description': 'Some description',
            'category': 'Eletricity',
            'buildingName': 'A',
            'roomName': '1',
            'possibleTransitions': []
    }

    const mockValues = [
        {"id": "1", "name": "Alfredo", "currentWorks": 5, "avaliation": 4},
        {"id": "1", "name": "Pedro", "currentWorks": 12, "avaliation": 1},
        {"id": "1", "name": "Quinaz", "currentWorks": 1, "avaliation": 5},
        {"id": "1", "name": "Ruizão", "currentWorks": 3, "avaliation": 4}
    ]

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <TicketInfo ticket={mockTicket}/>
            <Filters/>
            <p>Employee selected: {employeeSelected === undefined ? '-----' : employeeSelected?.name}</p>
            <Employees employees={mockValues}/>
            <div className='flex space-x-4'>
                <button className='w-1/2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded'>
                    Delive work
                </button>
                <button className='w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded'>
                    Delete ticket
                </button>
            </div>
        </div>
    )
}