
import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Loading } from '../components/Various';
import { ErrorView } from '../errors/Error';
import { useFetch } from '../hooks/useFetch';
import { Device, QrCode } from '../models/Models';
import { Action, Entity } from '../models/QRJsonModel';
import { QRCODE_URL_API, ROOM_DEVICE_URL_API, ROOM_URL_API } from '../Urls';
import { ActionComponent } from '../components/ActionComponent';
import './Popup.css';
import { getEntitiesOrUndefined, getActionsOrUndefined, getEntityOrUndefined, getProblemOrUndefined } from "../models/ModelUtils"
import { useFetchImage } from '../hooks/useFetchImage';
import Popup from 'reactjs-popup';
import { AiFillCloseCircle } from 'react-icons/ai';
import { MdDelete, MdOutlineCategory } from 'react-icons/md';

export function RoomDevice({deviceId, setDeleted}: {
    deviceId: number, 
    setDeleted: React.Dispatch<React.SetStateAction<boolean>>
}) {
    const [popup, setPopup] = useState(false)
    const [url, setUrl] = useState<string>('')

    const { companyId, buildingId, roomId } = useParams()

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)

    const { isFetching, result, error } = useFetch<Device>(ROOM_DEVICE_URL_API(companyId, buildingId, roomId, deviceId), init)

    useEffect(() => {if(action?.name === 'remove-room-device') setDeleted(true)})

    switch (action?.name) {
        case 'remove-room-device': return <ActionComponent action={action} returnComponent={null}/>
    }

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    function PopupComp() {
        const [imgUrl, setImgUrl] = useState<string>();
        
        const initValues: RequestInit = {
            method: action?.method,
            credentials: 'include',
            headers: { 'Request-Origin': 'WebApp' }
        }

        const init = useMemo(() => initValues, [])

        const { isFetching, result: result, error } = useFetchImage<Blob>(QRCODE_URL_API(url), init)
        
        useEffect(() => {
            if(result?.body?.type === 'success') {
                const blob = result.body.entity
                let objectURL = URL.createObjectURL(blob);    
                setImgUrl(objectURL);
            }
        }, [result])

        if (error) return <ErrorView/>
        return (
            <Popup className='popup-overlay' open={popup} onClose={() => setPopup(false)} modal>
                <div className='bg-white p-8 space-y-3 rounded-lg'>
                    {isFetching ? <Loading/> : (
                    <>
                        <button onClick={() => setPopup(false)}>
                            <AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "2.0em" }}/>
                        </button>
                        <div className="image overflow-hidden">
                            <img className="h-auto w-full mx-auto"src={imgUrl}/>
                        </div>
                    </>)}
                </div>
            </Popup>
        )
    } 
    
    function DeviceInfo({entity}: {entity: Entity<Device> | undefined}) {

        if(!entity) return null
        const device = entity.properties

        return (
            <div className='flex items-center space-x-2 text-gray-900'>
                <MdOutlineCategory/><span>: {device.category} </span>
            </div>
        )
    }

    function DeviceActions({ actions }: {actions: Action[] | undefined}) {

        if(!actions) return null

        let componentsActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'remove-room-device': return (
                        <button key={idx} onClick={() => {setAction(action);}} className="bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-2 rounded">
                            <MdDelete/>
                        </button>
                    )
            }
        })

        return <div className="flex space-x-2"> {componentsActions} </div>
    }

    function QrCodeSection({ entities, entity }: { entities?: Entity<QrCode>[], entity: Entity<Device> | undefined}) {    
        
        const qrcode = entities?.at(0)
        const url = qrcode?.properties.qrcode
        const state = entity?.properties.state

        if(!url) return null

        function QrCodeActions({ actions }: {actions: Action[] | undefined}) {
            if(!actions) return null
    
            let componentsActions = actions?.map((action, idx) => {
                switch(action.name) {
                case 'generate-new-qrcode': return (
                        <button key={idx} onClick={() => {setAction(action); setPopup(!popup); setUrl(url!!)}} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                            {action.title}
                        </button>
                    )
                }
            })
            return (<div className="flex space-x-2"> 
                        {componentsActions} 
                        {state === 'active' && <button onClick={() => {setAction(undefined); setPopup(!popup); setUrl(url!!)}} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                            Get QR Code
                        </button>}
                    </div>)
        }    

        return <div><QrCodeActions actions={qrcode?.actions}/><PopupComp/></div>
}

    return (
        <div className='w-full space-y-3'>
            <DeviceInfo entity={getEntityOrUndefined(result?.body)} />
            <QrCodeSection entity={getEntityOrUndefined(result?.body)} entities={getEntitiesOrUndefined(result?.body)}/>
            <DeviceActions actions={getActionsOrUndefined(result?.body)}/>
        </div>
    )
}