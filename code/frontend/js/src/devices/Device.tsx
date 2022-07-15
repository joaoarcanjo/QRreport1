import { useMemo, useState } from "react";
import { FaEdit } from "react-icons/fa";
import { useParams } from "react-router-dom";
import { Loading } from "../components/Various";
import { DisplayError } from "../Error";
import { useFetch } from "../hooks/useFetch";
import { Device } from "../models/Models";
import { Action, Entity } from "../models/QRJsonModel";
import { UpdateRoom } from "../room/UpdateRoom";
import { DEVICE_URL_API, ROOM_URL_API } from "../Urls";
import { ActionComponent } from "../user/profile/ActionRequest";
import { ChangeCategory } from "./ChangeCategory";
import { UpdateDevice } from "./UpdateDevice";
import { getEntitiesOrUndefined, getActionsOrUndefined, getEntityOrUndefined } from "../models/ModelUtils"
import { Anomalies } from "../anomaly/ListAnomalies";


export function DeviceRep() {

    const { deviceId } = useParams()

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')

    const { isFetching, isCanceled, cancel, result, error } = useFetch<Device>(DEVICE_URL_API(deviceId), init)

    switch (action?.name) {
        case 'create-anomaly': return <ActionComponent action={action} extraInfo={payload} returnComponent={<DeviceRep/>} />
        case 'deactivate-device': return <ActionComponent action={action} returnComponent={<DeviceRep/>} />
        case 'activate-device': return <ActionComponent action={action} returnComponent={<DeviceRep/>} />
        case 'update-device': return <ActionComponent action={action} extraInfo={payload} returnComponent={<DeviceRep/>} />
        case 'change-device-category': return <ActionComponent action={action} extraInfo={payload} returnComponent={<DeviceRep/>} />
    }
    
    if (isFetching) return <Loading/>
    if (isCanceled) return <p>Canceled</p>
    if (error !== undefined) return <DisplayError error={error}/>

    function DeviceState({state}: {state: string}) {

        const stateColor = state === 'inactive' ? 'bg-red-600' : 'bg-green-600';
        const stateElement = <span className={`${stateColor} py-1 px-2 rounded text-white text-sm`}>{state}</span>
        
        return <span className="ml-auto">{stateElement}</span>
    }

    function DeviceInfo({entity}: {entity: Entity<Device> | undefined}) {

        const [updateAction, setUpdateAction] = useState<Action>()
        if(!entity) return null

        const device = entity.properties

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{device.name}</span>
                        {entity.actions?.map(action => {
                            if(action.name === 'update-device') {
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
                        <p> Category: {device.category} </p>
                    </div>
                    <div> <DeviceState state={device.state}/> </div>
                    {updateAction && <UpdateDevice action={updateAction} setAction={setAction} setAuxAction={setUpdateAction} setPayload={setPayload}/>}
                </div>
            </div>
        )
    }

    function DeviceActions({ actions }: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)
        if(!actions) return null

        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'deactivate-device': return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'activate-device': return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                            {action.title}
                        </button>
                    )
                case 'change-device-category': return (
                    <button onClick={() => setAuxAction(action)} className="w-1/2 bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>
                )
            }
        })

        return (
            <>
                <div className="flex space-x-2"> {componentsActions} </div>
                {auxAction?.name === 'change-device-category' && 
                <ChangeCategory action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        ) 
    }

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <DeviceInfo entity={getEntityOrUndefined(result?.body)}/>
            <DeviceActions actions={getActionsOrUndefined(result?.body)}/>
            <Anomalies entities={getEntitiesOrUndefined(result?.body)} setAction={setAction} setPayload={setPayload}/>
        </div>
    )

}