import { useState } from "react"
import { Link } from "react-router-dom"
import { Building } from "../models/Models"
import { getSpecificEntity } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { CreateBuilding } from "./CreateBuilding"

function BuildingItem({entity}: {entity: Entity<Building>}) {
    const building = entity.properties

    const bgColor = building.state === 'active' ? 'bg-white' : 'bg-red-100'
    
    return (
        <div>
            <Link to={`buildings/${building.id}`}>
                <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 shadow-md hover:bg-gray-200 divide-y space-y-4`}> 
                    <h5 className='text-xl font-md text-gray-900'>{building.name}</h5>
                    <p>Number of rooms: {building.numberOfRooms}</p>
                </div>
            </Link> 
        </div>
    )
}

export function BuildingsActions({companyId, entities, setAction, setPayload }: {  
    companyId: string | undefined,
    entities?: Entity<Building>[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

    if(!entities || !setAction || !setAuxAction || !setPayload) return null 

    const collection = getSpecificEntity(['building', 'collection'], 'company-buildings', entities)
    const actions = collection?.actions
    if (!actions) return null
    
    let componentsActions = actions?.map(action => {
        switch(action.name) {
            case 'create-building': return (
                    <button onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                        {action.title}
                    </button>
                )
        }
    })

    return (
        <>
            <div className="flex space-x-2">{componentsActions} </div>
            {auxAction?.name === 'create-building' && 
            <CreateBuilding action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
        </>
    )
}

export function Buildings({ entities }: { entities?: Entity<Building>[]}) {
    if (!entities) return null
    const collection = getSpecificEntity(['building', 'collection'], 'company-buildings', entities)
    const buildings = collection?.entities
    if (!buildings) return null

    return (
        <div className="space-y-3">
            {buildings.map((entity, idx) => {
                if (entity.class.includes('building') && entity.rel?.includes('item')) 
                    return <BuildingItem key={idx} entity={entity}/>
            })}
        </div>
    )
}