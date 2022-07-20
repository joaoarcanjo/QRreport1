import { useMemo, useState } from "react";
import { FaEdit } from "react-icons/fa"
import { Link, Outlet, useParams } from "react-router-dom"
import { Loading, StateComponent } from "../components/Various";
import { useFetch } from "../hooks/useFetch";
import { Building } from "../models/Models";
import { Action, Entity } from "../models/QRJsonModel";
import { BUILDING_URL_API } from "../Urls";
import { ActionComponent } from "../components/ActionComponent";
import { getEntitiesOrUndefined, getActionsOrUndefined, getEntityOrUndefined, getProblemOrUndefined, getLink, getSpecificEntity } from "../models/ModelUtils"
import { UpdateBuilding } from "./UpdateBuilding";
import { Rooms, RoomsActions } from "../room/ListRooms";
import { ChangeManager } from "./ChangeManager";
import { ErrorView } from "../errors/Error";


export function BuildingRep() {

    const { companyId, buildingId } = useParams()
    
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')

    const { isFetching, result, error } = useFetch<Building>(BUILDING_URL_API(companyId, buildingId), init)
    
    switch (action?.name) {
        case 'update-building': return <ActionComponent action={action} extraInfo={payload} returnComponent={<BuildingRep/>}/>
        case 'change-building-manager': return <ActionComponent action={action} extraInfo={payload} returnComponent={<BuildingRep/>} />
        case 'create-building': return <ActionComponent action={action} extraInfo={payload} returnComponent={<BuildingRep/>} />
        case 'create-room': return <ActionComponent action={action} extraInfo={payload} returnComponent={<BuildingRep/>} />
        case 'deactivate-building': return <ActionComponent action={action} returnComponent={<BuildingRep/>} />
        case 'activate-building': return <ActionComponent action={action} returnComponent={<BuildingRep/>} />
    }

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    function BuildingInfo({entity}: {entity: Entity<Building> | undefined}) {

        const [updateAction, setUpdateAction] = useState<Action>()
        if(!entity) return null

        const building = entity.properties

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{building.name}</span>
                        {entity.actions?.map((action, idx) => {
                            if(action.name === 'update-building') {
                                return (
                                    !updateAction && (
                                    <button key={idx} className="my-1" onClick={()=> setUpdateAction(action)}>
                                        <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                                    </button>)
                                )
                            }
                        })}
                    </div>
                    <StateComponent state={building.state} timestamp={building.timestamp}/>
                    <div className='flex flex-col space-y-4'>
                        {/*<p> Number of rooms: {building.numberOfRooms} </p>*/}
                    </div>
                    {updateAction && <UpdateBuilding action={updateAction} setAction={setAction} setAuxAction={setUpdateAction} setPayload={setPayload}/>}
                </div>
            </div>
        )
    }

    function BuildingActions({ actions }: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)
        if(!actions) return null

        let componentsActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'deactivate-building': return (
                        <button key={idx} onClick={() => setAction(action)} className="bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'activate-building': return (
                        <button key={idx} onClick={() => setAction(action)} className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'change-building-manager': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>
                )
            }
        })

        return (
            <>
                <div className="flex space-x-2"> {componentsActions} </div>
                {auxAction?.name === 'change-building-manager' && 
                <ChangeManager action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
            </>
        ) 
    }
    
    const entities = getEntitiesOrUndefined(result?.body)
    if(!entities) return <ErrorView/>
    const collection = getSpecificEntity(['room', 'collection'], 'building-rooms', entities)
    if(!collection) return <ErrorView/>

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <BuildingInfo entity={getEntityOrUndefined(result?.body)}/>
            <BuildingActions actions={getActionsOrUndefined(result?.body)}/>
            <RoomsActions entities={getEntitiesOrUndefined(result?.body)} setAction={setAction} setPayload={setPayload}/>
            <Rooms collection={collection}/>
        </div>
    )
}