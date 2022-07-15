import { useState } from "react"
import { MdExpandMore, MdExpandLess } from "react-icons/md"
import { Link } from "react-router-dom"
import { Device } from "../models/Models"
import { getSpecificEntity } from "../models/ModelUtils"
import { Entity } from "../models/QRJsonModel"
import { RoomDevice } from "../room/RoomDevice"

function RoomDeviceItem({entity}: {entity: Entity<Device>}) {
    const device = entity.properties

    const bgColor = device.state === 'active' ? 'bg-white' : 'bg-red-100'

    const [moreInfo, showMoreInfo] = useState(false)
    
    return (
    <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 hover:bg-gray-100 divide-y space-y-4`}> 
        <div className="items-center">
            <div><span className='text-xl font-md text-gray-900'>{device.name}</span></div>
        </div>
        <div>
            <button className='my-1' onClick={() => showMoreInfo(!moreInfo)}>
                {!moreInfo && <MdExpandMore style= {{ color: 'blue', fontSize: '2em' }} />}
                { moreInfo && <MdExpandLess style= {{ color: 'blue', fontSize: '2em' }} />}
            </button>
            <div className='space-y-4'>{moreInfo && <RoomDevice deviceId={device.id}/>}</div>
        </div>
    </div>)
}

export function RoomDevices({ entities }: { entities?: Entity<Device>[]}) {
    if (!entities) return null
    const collection = getSpecificEntity(['device', 'collection'], 'room-devices', entities)
    const devices = collection?.entities
    if (!devices) return null

    return (
        <div className="space-y-3">
        {devices.map((entity, idx) => {
            if (entity.class.includes('device') && entity.rel?.includes('item')) 
                return <RoomDeviceItem key={idx} entity={entity}/>
        })}
        </div>
    )
}