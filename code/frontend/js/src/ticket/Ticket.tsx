import { useMemo, useState } from "react";
import { MdExpandLess, MdExpandMore, MdOutlineMeetingRoom } from "react-icons/md";
import { useParams } from "react-router-dom";
import { Loading } from "../components/Various";
import { DisplayError } from "../Error";
import { useFetch } from "../hooks/useFetch";
import { Ticket } from "../models/Models";
import { TICKETS_URL,  TICKET_URL_API } from "../Urls";
import { getActionsOrUndefined, getEntityOrUndefined, getSpecificEntity } from '../models/ModelUtils';
import { Action, Entity } from "../models/QRJsonModel";
import { FaEdit, FaRegBuilding } from "react-icons/fa";
import { ActionComponent } from "../user/profile/ActionRequest";
import { SetEmployeeAction } from "./SetEmployeeAction";
import { UpdateTicket } from "./UpdateTicket";
import { TicketRate } from "./TicketRate";
import { UpdateState } from "./TicketState";
import { ListComments } from "../comment/ListComments";

export function TicketRep() {

    const { ticketId } = useParams()
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')

    console.log(`Action:, ${action}`)
    console.log(`payload:, ${payload}`)

    //todo: initValues will be the same for all get requests
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])

    const { isFetching, isCanceled, cancel, result, error } = useFetch<Ticket>(TICKET_URL_API(ticketId), init)
    if (isFetching) return <Loading/>
    if (isCanceled) return <p>Canceled</p>
    if (error !== undefined) return <DisplayError error={error}/>

    switch (action?.name) {
        case 'delete-ticket': return <ActionComponent action={action} redirectUrl={TICKETS_URL} />
        case 'set-employee': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'remove-employee': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'update-ticket-state': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'update-ticket': return <UpdateTicket action={action}/>
        case 'create-comment': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'delete-comment': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
        case 'update-comment': return <ActionComponent action={action} extraInfo={payload} returnComponent={<TicketRep/>}/>
    }

    function UpdateAction({action}: {action: Action | undefined}) {
        return action !== undefined ? (
            <button onClick={() => setAction(action)} className="my-1">
                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
            </button>
        ): null
    }   

    function TicketInfo({entity, actions}: {entity: Entity<Ticket> | undefined, actions?: Action[] }) {
        const [updateFLag, setUpdateFlag] = useState(false)
        
        if (!entity) return null
        const ticket = entity.properties

        const roomEntity = getSpecificEntity(["room"], "ticket-room", entity.entities)
        const buildingEntity = getSpecificEntity(["building"], "ticket-building", entity.entities)
        const deviceEntity = getSpecificEntity(["device"], "ticket-device", entity.entities)
        const personEntity = getSpecificEntity(["person"], "ticket-author", entity.entities)

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='flex flex-col space-y-4 device-y'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{ticket.subject}</span>
                        <UpdateAction action={actions?.find(action => action.name === 'update-ticket')}/> 
                    </div>
                    <div className='flex items-center space-x-4'>
                        <div className='flex items-center'>
                            <FaRegBuilding/> 
                            <span>: {buildingEntity?.properties.name}</span>
                        </div>
                        <div className='flex items-center'>
                            <MdOutlineMeetingRoom/> 
                            <span>: {roomEntity?.properties.name} </span>
                        </div>
                        <span>Device: {deviceEntity?.properties.name} </span>
                    </div>
                    <span>Author: {personEntity?.properties.name}</span>
                    <p className="text-sm text-slate-600"> {ticket.description} </p>
                </div>
                {actions?.map(action => {
                    if(action.name === 'update-ticket-state') {
                        return(
                            <div>
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
                        return <TicketRate action={action} setAction={setAction} setPayload={setPayload}/>
                    }
                })}
                <TicketActions actions={getActionsOrUndefined(result?.body)}/>
            </div>
        )
    }

    function TicketActions({ actions }: {actions?: Action[] | undefined}) {
        
        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'delete-ticket': return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'set-employee': return (
                        <button onClick={() => setAuxAction(action)} className="w-1/2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'remove-employee': return (
                    <button onClick={() => setAction(action)} className="w-1/2 bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>
                )
            }
        })

        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'set-employee' && 
                <SetEmployeeAction action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        )
    }
    
    const entity = getEntityOrUndefined(result?.body)
    if(!entity) return null
    const comments = getSpecificEntity(["comment", "collection"], "ticket-comments", entity.entities)
    return (
        <div className="mx-auto my-auto">
            <div className='md:flex w-full px-3 pt-3 space-y-3 no-wrap md:-mx-2'>
                <div className="md:w-5/12 md:mx-2 space-x-4">
                    <TicketInfo entity={entity} actions={getActionsOrUndefined(result?.body)}/>
                </div>
                <div className="md:w-7/12 md:mx-2 space-x-4 w-full">
                    <ListComments entity={comments} setAction={setAction} setPayload={setPayload}/>
                </div>
            </div>
        </div>
    )
}