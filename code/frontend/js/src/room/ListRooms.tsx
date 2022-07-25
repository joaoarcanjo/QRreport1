import { useMemo, useState } from "react"
import { Link, Outlet } from "react-router-dom"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Room } from "../models/Models"
import { getEntityLink, getEntityOrUndefined, getProblemOrUndefined, getSpecificEntity } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { CreateRoom } from "./CreateRoom"

export function RoomsActions({ entities, setAction, setPayload }: {  
    entities?: Entity<Room>[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {
    const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

    if(!entities || !setAction || !setAuxAction) return null 
    
    const collection = getSpecificEntity(['room', 'collection'], 'building-rooms', entities)
    const actions = collection?.actions
    if (!actions) return null
    
    let componentsActions = actions?.map((action, idx) => {
        switch(action.name) {
            case 'create-room': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                        {action.title}
                    </button>
                )
        }
    })

    return (
        <>
            <div className="flex space-x-2">{componentsActions} </div>
            {auxAction?.name === 'create-room' && 
            <CreateRoom action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
        </>
    )
}

export function Rooms({ collection }: { collection?: Entity<Collection>}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])

    const [currentUrl, setCurrentUrl] = useState('')
    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
        
    function RoomItem({entity}: {entity: Entity<Room>}) {
        const room = entity.properties

        const bgColor = room.state === 'active' ? 'bg-white' : 'bg-red-100'
        
        return (
            <div>
                <Link to={`rooms/${room.id}`}>
                    <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 hover:bg-gray-200 shadow-md`}>  
                        <h5 className='text-xl font-md text-gray-900'>{room.name}</h5>
                    </div>
                </Link>
            </div>
        )
    }

    if (result?.body) collection = getEntityOrUndefined(result.body)
    const rooms = collection?.entities

    if (!rooms || !collection) return null
    return (
        <>
            <div className="space-y-3">
                {rooms.map((entity, idx) => {
                    if (entity.class.includes('room') && entity.rel?.includes('item')) 
                        return <RoomItem key={idx} entity={entity}/>
                })}
            </div>
            <CollectionPagination 
                collection={collection.properties} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getEntityLink('pagination', collection)}/>
            <Outlet/>
        </>
    )
}
