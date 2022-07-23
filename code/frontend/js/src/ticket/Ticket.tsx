import { useMemo, useState } from "react";
import { MdExpandLess, MdExpandMore } from "react-icons/md";
import { Link, Navigate, useParams } from "react-router-dom";
import { Loading } from "../components/Various";
import { ErrorView } from "../errors/Error";
import { useFetch } from "../hooks/useFetch";
import { Ticket } from "../models/Models";
import { LOGIN_URL, TICKETS_URL,  TICKET_URL,  TICKET_URL_API } from "../Urls";
import { getActionsOrUndefined, getEntityOrUndefined, getSpecificEntity, getProblemOrUndefined } from '../models/ModelUtils';
import { Action, Entity } from "../models/QRJsonModel";
import { FaEdit, FaRegBuilding, FaToilet, FaToolbox } from "react-icons/fa";
import { ActionComponent } from "../components/ActionComponent";
import { SetEmployeeAction } from "./SetEmployeeAction";
import { UpdateTicket } from "./UpdateTicket";
import { TicketRate } from "./TicketRate";
import { UpdateState } from "./TicketState";
import { ListComments } from "../comment/ListComments";
import { GroupTicket } from "./GroupTicket";
import { ADMIN_ROLE, EMPLOYEE_ROLE, MANAGER_ROLE, useLoggedInState, USER_ROLE } from "../user/Session"
import { TbPencil } from "react-icons/tb";
import { BsBuilding, BsDoorClosed } from "react-icons/bs";

export function TicketRep() {

    //todo: initValues will be the same for all get requests
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const { ticketId } = useParams()
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const [currentUrl, setCurrentUrl] = useState('')
    const userSession = useLoggedInState()

    const init = useMemo(() => initValues ,[])

    const { isFetching, result, error } = useFetch<Ticket>(currentUrl, init)

    if(userSession?.isLoggedIn && currentUrl === '') 
        setCurrentUrl(TICKET_URL_API(ticketId))
    else if(!userSession?.isLoggedIn) 
        return <Navigate to={LOGIN_URL}/>

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    switch (action?.name) {
        case 'delete-ticket': return <ActionComponent action={action} redirectUrl={TICKETS_URL} />
        case 'add-rate': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'set-employee': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'remove-employee': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'update-state': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'update-ticket': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'group-ticket': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'create-comment': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'delete-comment': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'update-comment': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
    }

    function UpdateAction({action, setUpdateFlag}: {
        action: Action | undefined, 
        setUpdateFlag: React.Dispatch<React.SetStateAction<boolean>>
    }) {
        return action !== undefined ? (
            <button onClick={() => setUpdateFlag(true)} className="my-1">
                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
            </button>
        ): null
    }   

    function TicketInfo({entity, actions, parent}: { entity: Entity<Ticket>, actions?: Action[], parent: Entity<number> | undefined}) {
        const [updateFLag, setUpdateFlag] = useState(false)
        const [updaTicketFlag, setUpdateTicketFlag] = useState(false)
        
        const ticket = entity.properties

        const roomEntity = getSpecificEntity(["room"], "ticket-room", entity.entities)
        const buildingEntity = getSpecificEntity(["building"], "ticket-building", entity.entities)
        const deviceEntity = getSpecificEntity(["device"], "ticket-device", entity.entities)
        const authorEntity = getSpecificEntity(["person"], "ticket-author", entity.entities)
        const employeeEntity = getSpecificEntity(["person"], "ticket-employee", entity.entities)
        const userRole = useLoggedInState()?.userRole
        
        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3 divide-y-2'>
                <div className='flex flex-col space-y-4 device-y'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{ticket.subject}</span>
                        <UpdateAction action={actions?.find(action => action.name === 'update-ticket')} setUpdateFlag={setUpdateTicketFlag}/> 
                    </div>
                    {updaTicketFlag &&
                        <UpdateTicket action={actions?.find(action => action.name === 'update-ticket')} setAction={setAction} setAuxAction={setUpdateTicketFlag} setPayload={setPayload}/>
                    }
                    <div className='flex items-center'>
                        <FaRegBuilding style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {buildingEntity?.properties.name}</span>
                    </div>
                    <div className='flex items-center'>
                        <BsDoorClosed style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {roomEntity?.properties.name} </span>
                    </div> 
                    <div className='flex items-center'>
                        <FaToilet style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {deviceEntity?.properties.name} </span>
                    </div>
                    <div className='flex'>
                        <Link to={`/persons/${authorEntity?.properties.id}`}>
                            <div className='flex items-center'>
                                <TbPencil style= {{ color: 'green', fontSize: "1.4em" }} />
                                <span>: {authorEntity?.properties.name}</span>
                            </div>
                        </Link>
                    </div>
                    {(((userRole === MANAGER_ROLE || userRole === ADMIN_ROLE || userRole === EMPLOYEE_ROLE) && (employeeEntity))) && <div className='flex'>
                        <Link to={`/persons/${employeeEntity?.properties.id}`}>
                            <div className='flex items-center'>
                                <FaToolbox style= {{ color: 'green', fontSize: "1.4em" }} />
                                <span>: {employeeEntity?.properties.name}</span>
                            </div>
                        </Link>
                    </div>}
                    <p className="text-sm text-slate-600"> {ticket.description} </p>
                    <div className='bg-blue-400 mr-auto py-1 px-2 rounded text-white text-sm'>
                        {userRole === USER_ROLE ? ticket.userState : ticket.employeeState} 
                    </div>
                </div>
                <div className="space-y-4">
                    <div></div>
                    {actions?.map((action, idx) => {
                        if(action.name === 'update-state') {
                            return(
                                <div key={idx}>
                                    <button className='text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2 inline-flex items-center' 
                                            onClick={() => setUpdateFlag(!updateFLag)}> 
                                        {!updateFLag && <MdExpandMore style= {{ color: 'white', fontSize: '2em' }} />}
                                        { updateFLag && <MdExpandLess style= {{ color: 'white', fontSize: '2em' }} />}
                                        Update state
                                    </button>
                                    {updateFLag && <UpdateState states={ticket.possibleTransitions} action={action} setAction={setAction} setPayload={setPayload}/>}
                                </div>  
                            ) 
                        }    
                        if(action.name === 'add-rate') {
                            return <TicketRate key={idx} action={action} setAction={setAction} setPayload={setPayload}/>
                        }
                    })}
                    <TicketActions actions={getActionsOrUndefined(result?.body)}/>
                    <ParentButton entity={parent}/>
                </div>
            </div>
        )
    }

    function TicketActions({ actions }: {actions?: Action[] | undefined}) {
        
        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        let componentsActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'delete-ticket': return (
                    <button key={idx} onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'set-employee': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'remove-employee': return (
                    <button key={idx} onClick={() => setAction(action)} className="w-1/2 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'group-ticket': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
            }
        })

        return (
            <>
                <div className="flex space-x-2"> {componentsActions} </div>
                {auxAction?.name === 'group-ticket' && 
                <GroupTicket action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
                {auxAction?.name === 'set-employee' && 
                <SetEmployeeAction action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        )
    }

    function ParentButton({entity}: {entity: Entity<number> | undefined}) {
        if(!entity) return null
        return (
            <Link to={TICKET_URL(entity.properties)}>
                <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2"> Parent </button>
            </Link>)
    }
    
    const entity = getEntityOrUndefined(result?.body)
    if(!entity) return null
    const comments = getSpecificEntity(["comment", "collection"], "ticket-comments", entity.entities)
    const parent = getSpecificEntity(["ticket"], "parent-ticket", entity.entities)
    
    return (
        <div className="mx-auto my-auto">
            <div className='md:flex w-full px-3 pt-3 space-y-3 no-wrap md:-mx-2'>
                <div className="md:w-5/12 md:mx-2 space-x-4">
                    <TicketInfo entity={entity} actions={getActionsOrUndefined(result?.body)} parent={parent}/>
                </div>
                <div className="md:w-7/12 md:mx-2 space-x-4 w-full">
                    <ListComments collection={comments} setAction={setAction} setPayload={setPayload}/>
                </div>
            </div>
        </div>
    )
}