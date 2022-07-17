import { useMemo, useState } from "react";
import { FaEdit } from "react-icons/fa";
import { Outlet, useParams } from "react-router-dom";
import { Loading } from "../components/Various";
import { ErrorView } from "../errors/Error";
import { useFetch } from "../hooks/useFetch";
import { Room } from "../models/Models";
import { Action, Entity } from "../models/QRJsonModel";
import { ROOM_URL_API } from "../Urls";
import { ActionComponent } from "../components/ActionComponent";
import { AddRoomDevice } from "./AddRoomDevice";
import { UpdateRoom } from "./UpdateRoom";
import { getEntitiesOrUndefined, getActionsOrUndefined, getEntityOrUndefined, getLink, getSpecificEntity } from "../models/ModelUtils"
import { RoomDevices } from "../devices/RoomDevices";
import { CollectionPagination } from "../pagination/CollectionPagination";

export function RoomRep() {
    
    const { companyId, buildingId, roomId } = useParams()

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const [currentUrl, setCurrentUrl] = useState(ROOM_URL_API(companyId, buildingId, roomId))

    const { isFetching, result, error } = useFetch<Room>(currentUrl, init)
    
    switch (action?.name) {
        case 'update-room': return <ActionComponent action={action} extraInfo={payload} returnComponent={<RoomRep/>} />
        case 'add-room-device': return <ActionComponent action={action} extraInfo={payload} returnComponent={<RoomRep/>} />
        case 'deactivate-room': return <ActionComponent action={action} returnComponent={<RoomRep/>} />
        case 'activate-room': return <ActionComponent action={action} returnComponent={<RoomRep/>} />
    }
    
    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    function RoomState({state}: {state: string}) {

        const stateColor = state === 'inactive' ? 'bg-red-600' : 'bg-green-600';
        const stateElement = <span className={`${stateColor} py-1 px-2 rounded text-white text-sm`}>{state}</span>
        
        return <span className="ml-auto">{stateElement}</span>
    }

    function RoomInfo({entity}: {entity: Entity<Room> | undefined}) {

        const [updateAction, setUpdateAction] = useState<Action>()
        if(!entity) return null

        const room = entity.properties

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{room.name}</span>
                        {entity.actions?.map(action => {
                            if(action.name === 'update-room') {
                                return (
                                    !updateAction && (
                                    <button className="my-1" onClick={()=> setUpdateAction(action)}>
                                        <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                                    </button>)
                                )
                            }
                        })}
                    </div>
                    <div className='flex flex-col space-y-4'>
                        <p> Number of reports: {room.numberOfReports} </p>
                    </div>
                    <div> <RoomState state={room.state}/> </div>
                    {updateAction && <UpdateRoom action={updateAction} setAction={setAction} setAuxAction={setUpdateAction} setPayload={setPayload}/>}
                </div>
            </div>
        )
    }

    function RoomActions({ actions }: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)
        if(!actions) return null

        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'deactivate-room': return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'activate-room': return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'add-room-device': return (
                    <button onClick={() => setAuxAction(action)} className="w-1/2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
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

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <RoomInfo entity={getEntityOrUndefined(result?.body)}/>
            <RoomActions actions={getActionsOrUndefined(result?.body)}/>
            <RoomDevices collection={collection}/>
            <CollectionPagination 
                collection={collection.properties} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}