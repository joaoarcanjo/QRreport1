import { useState } from "react"
import { Link } from "react-router-dom"
import { Room } from "../models/Models"
import { getSpecificEntity } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection } from "../pagination/CollectionPagination"
import { CreateRoom } from "./CreateRoom"

function RoomItem({entity}: {entity: Entity<Room>}) {
    const room = entity.properties

    const bgColor = room.state === 'active' ? 'bg-white' : 'bg-red-100'
    
    return (
        <div>
            <Link to={`rooms/${room.id}`}>
                <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 hover:bg-gray-200 shadow-md`}>  
                    <h5 className='text-xl font-md text-gray-900'>{room.name}</h5>
                    <p>Number of reports: {room.numberOfReports}</p>
                </div>
            </Link>
        </div>
    )
}

export function RoomsActions({ entities, setAction, setPayload }: {  
    entities?: Entity<Room>[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {
    const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

    if(!entities || !setAction || !setAuxAction || !setPayload) return null 
    
    const collection = getSpecificEntity(['room', 'collection'], 'building-rooms', entities)
    const actions = collection?.actions
    if (!actions) return null
    
    let componentsActions = actions?.map(action => {
        switch(action.name) {
            case 'create-room': return (
                    <button onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
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
    if (!collection) return null
    const rooms = collection?.entities
    if (!rooms) return null

    return (
        <div className="space-y-3">
            {rooms.map((entity, idx) => {
                if (entity.class.includes('room') && entity.rel?.includes('item')) 
                    return <RoomItem key={idx} entity={entity}/>
            })}
        </div>
    )
}
