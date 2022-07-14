import { Device } from "../models/Models"
import { getSpecificEntity } from "../models/ModelUtils"
import { Entity } from "../models/QRJsonModel"

function DeviceItem({entity}: {entity: Entity<Device>}) {
    const device = entity.properties

    console.log(entity.entities)
    const bgColor = device.state === 'active' ? 'bg-white' : 'bg-red-100'
    
    return (
        <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 shadow-md`}> 
            <div className="flex items-center">
                <span className='text-xl font-md text-gray-900'>{device.name}</span>
            </div>
        </div>
    )
}

export function Devices({ entities }: { entities?: Entity<Device>[]}) {
    if (!entities) return null
    const collection = getSpecificEntity(['device', 'collection'], 'room-devices', entities)
    const devices = collection?.entities
    if (!devices) return null

    return (
        <div className="space-y-3">
        {devices.map((entity, idx) => {
            if (entity.class.includes('device') && entity.rel?.includes('item')) 
                return <DeviceItem key={idx} entity={entity}/>
        })}
        </div>
    )
}