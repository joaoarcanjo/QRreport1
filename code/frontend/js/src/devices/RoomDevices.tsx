import { useMemo, useState } from "react"
import { MdExpandMore, MdExpandLess } from "react-icons/md"
import { Link, Outlet } from "react-router-dom"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Device, Room } from "../models/Models"
import { getEntityLink, getEntityOrUndefined, getProblemOrUndefined } from "../models/ModelUtils"
import { Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { RoomDevice } from "../room/RoomDevice"
import { DEVICE_URL } from "../Urls"

export function RoomDevices({ roomEntity, collection }: {roomEntity: Entity<Room>, collection?: Entity<Collection>}) {

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

    function RoomDeviceItem({entity}: {entity: Entity<Device>}) {
        const device = entity.properties
    
        const bgColor = device.state === 'active' ? 'bg-white' : 'bg-red-100'
    
        const [moreInfo, showMoreInfo] = useState(false)
        const [isDeleted, setDeleted] = useState(false)
    
        if(isDeleted) return <></>
        
        return (
        <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 hover:bg-gray-100 divide-y space-y-4`}> 
            <div className="flex items-center">
                <Link to={DEVICE_URL(device.id)}>
                    <span className='text-xl font-md text-gray-900'> {device.name} </span>
                </Link>
            </div>
            <div>
                <button className='my-1' onClick={() => showMoreInfo(!moreInfo)}>
                    {!moreInfo && <MdExpandMore style= {{ color: 'blue', fontSize: '2em' }} />}
                    { moreInfo && <MdExpandLess style= {{ color: 'blue', fontSize: '2em' }} />}
                </button>
                <div className='space-y-4'>{moreInfo && (
                    <RoomDevice roomState={roomEntity.properties.state} deviceId={device.id} setDeleted={setDeleted}/>)}</div>
            </div>
        </div>)
    }

    if (result?.body) collection = getEntityOrUndefined(result.body)
    const devices = collection?.entities

    if (!devices || !collection) return null
    
    return (
        <>
            <div className="space-y-3">
                {devices.map((entity, idx) => {
                    if (entity.class.includes('device') && entity.rel?.includes('item')) 
                        return <RoomDeviceItem key={idx} entity={entity}/>
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