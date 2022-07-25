import { useMemo, useState } from "react";
import { FaEdit } from "react-icons/fa";
import { Navigate, useParams } from "react-router-dom";
import { Loading, StateComponent } from "../components/Various";
import { ErrorView } from "../errors/Error";
import { useFetch } from "../hooks/useFetch";
import { Room } from "../models/Models";
import { Action, Entity } from "../models/QRJsonModel";
import { LOGIN_URL, ROOM_URL_API } from "../Urls";
import { ActionComponent } from "../components/ActionComponent";
import { AddRoomDevice } from "./AddRoomDevice";
import { UpdateRoom } from "./UpdateRoom";
import { getEntitiesOrUndefined, getActionsOrUndefined, getEntityOrUndefined, getSpecificEntity } from "../models/ModelUtils"
import { RoomDevices } from "../devices/RoomDevices";
import { useLoggedInState } from "../user/Session";

export function RoomRep() {
    
    const { companyId, buildingId, roomId } = useParams()

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const [currentUrl, setCurrentUrl] = useState('')
    const userSession = useLoggedInState()

    const { isFetching, result, error } = useFetch<Room>(currentUrl, init)

    if(userSession?.isLoggedIn && currentUrl === '') 
        setCurrentUrl(ROOM_URL_API(companyId, buildingId, roomId))
    else if(!userSession?.isLoggedIn) 
        return <Navigate to={LOGIN_URL}/>
    
    switch (action?.name) {
        case 'update-room': return <ActionComponent action={action} extraInfo={payload} returnComponent={<RoomRep/>} />
        case 'add-room-device': return <ActionComponent action={action} extraInfo={payload} returnComponent={<RoomRep/>} />
        case 'deactivate-room': return <ActionComponent action={action} returnComponent={<RoomRep/>} />
        case 'activate-room': return <ActionComponent action={action} returnComponent={<RoomRep/>} />
    }
    
    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    function RoomInfo({entity}: {entity: Entity<Room> | undefined}) {

        const [updateAction, setUpdateAction] = useState<Action>()
        if(!entity) return null

        const room = entity.properties

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{room.name}</span>
                        {entity.actions?.map((action, idx) => {
                            if(action.name === 'update-room') {
                                return (
                                    !updateAction && (
                                    <button key={idx} className="my-1" onClick={()=> setUpdateAction(action)}>
                                        <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                                    </button>)
                                )
                            }
                        })}
                    </div>
                    <StateComponent state={room.state} timestamp={room.timestamp}/>
                    {updateAction && <UpdateRoom action={updateAction} setAction={setAction} setAuxAction={setUpdateAction} setPayload={setPayload}/>}
                </div>
            </div>
        )
    }

    function RoomActions({ actions }: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)
        if(!actions) return null

        let componentsActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'deactivate-room': return (
                        <button key={idx} onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'activate-room': return (
                        <button key={idx} onClick={() => setAction(action)} className="w-1/2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'add-room-device': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>
                )
            }
        })

        return (
            <>
                <div className="flex space-x-2"> {componentsActions} </div>
                {auxAction?.name === 'add-room-device' && 
                <AddRoomDevice action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        ) 
    }

    const entities = getEntitiesOrUndefined(result?.body)
    if(!entities) return <ErrorView/>
    const collection = getSpecificEntity(['device', 'collection'], 'room-devices', entities)
    if(!collection) return <ErrorView/>
    const entity = getEntityOrUndefined(result?.body)
    if(!entity) return <ErrorView/>

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <RoomInfo entity={entity}/>
            <RoomActions actions={getActionsOrUndefined(result?.body)}/>
            <RoomDevices roomEntity={entity} collection={collection}/>
        </div>
    )
}