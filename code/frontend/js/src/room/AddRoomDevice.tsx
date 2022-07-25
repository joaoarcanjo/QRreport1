import { useState, useMemo } from "react"
import { AiFillCloseCircle } from "react-icons/ai"
import { Outlet } from "react-router-dom"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Device } from "../models/Models"
import { getProblemOrUndefined, getEntityOrUndefined, getLink, getPropertiesOrUndefined } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { BASE_URL_API } from "../Urls"

export function AddRoomDevice({action, setAction, setAuxAction, setPayload}: {
    action: Action,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const property = action.properties.find(prop => {if(prop.name === 'device'){ return prop}})
    const href = property?.possibleValues?.href
    const url = href === undefined || null ? '' : BASE_URL_API + href

    const [device, setDevice] = useState<Device>()
    const init = useMemo(() => initValues ,[])
    const [currentUrl, setCurrentUrl] = useState(url)

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if (!action || !setPayload || !setAction) return null

    if (isFetching) return <Loading/>
    if (error) return <ErrorView/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    function DeviceItem({entity}: {entity: Entity<Device>}) {
        if (!entity) return null;
        const device = entity.properties
        const state = device.state
        const stateColor = state === 'active' ? 'bg-white' : 'bg-red-200'

        return (
            <div className={`flex p-1 ${stateColor} rounded-lg border border-gray-400`}>  
                <div className='w-full flex space-x-4'>
                    <h5 className='font-md text-gray-900'>{device.name}</h5>
                </div>
                {state === 'active' &&
                <div className='w-full flex justify-end' >
                    <button 
                        className='px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'
                        onClick= {() => setDevice(device)}>
                        Select
                    </button>
                </div>}
            </div>
        )
    }

    function Devices({entity}: { entity: Entity<Collection> | undefined }) {

        if(!entity) return null;

        return (
            <div className='flex flex-col space-y-3'>
                {entity.entities!!.map((device, idx) => <DeviceItem key={idx} entity={device}/>)}
            </div>
        )
    }

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200 shadow-md">
            <button onClick={() => setAuxAction(undefined)}>
                <AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
            </button>
            <p>Device selected: {device === undefined ? '-----' : device?.name}</p>
            <Devices entity={getEntityOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
            <div className='flex space-x-4'>
                <button className='w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded'
                    onClick= {() => {setAction(action); setPayload(JSON.stringify({deviceId: device?.id}))}}>
                    {action.title}
                </button>
            </div>
        </div>
    )
}
