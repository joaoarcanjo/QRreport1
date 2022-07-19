import { useMemo, useState } from "react"
import { GrUpdate } from "react-icons/gr"
import { MdDelete } from "react-icons/md"
import { Outlet } from "react-router-dom"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Anomaly, } from "../models/Models"
import { getEntityLink, getEntityOrUndefined, getProblemOrUndefined } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { InputAnomaly } from "./InputAnomaly"

function AnomalyItem({entity, setAction, setPayload }: {  
    entity: Entity<Anomaly>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {
    const anomaly = entity.properties

    return (
        <div>
            <div className={`p-5 bg-white rounded-lg border border-gray-200 shadow-md space-y-4`}> 
                <h5 className='text-xl font-md text-gray-900'>{anomaly.anomaly}</h5>
                <AnomalyActions entity={entity} setAction={setAction} setPayload={setPayload}/>
            </div>
        </div>
    )
}

export function AnomalyActions({entity, setAction, setPayload }: { 
    entity: Entity<Anomaly>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

    const actions = entity?.actions
    if (!actions) return null
    
    let componentsActions = actions?.map((action, idx) => {
        switch(action.name) {
            case 'update-anomaly': return !auxAction && (
                    <button key={idx} onClick={() => setAuxAction(action)} className="text-white bg-yellow-400 hover:bg-yellow-600 rounded-lg px-2">
                        <GrUpdate/>
                    </button>
                )
            case 'delete-anomaly': return !auxAction && (
                <button key={idx} onClick={() => setAction(action)} className="bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-2 rounded">
                    <MdDelete/>
                </button>
            )
        }
    })

    return (
        <>
            <div className="flex space-x-2">{componentsActions} </div>
            {auxAction?.name === 'update-anomaly' && 
            <InputAnomaly action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
        </>
    )
}

export function AnomaliesActions({entity, setAction, setPayload }: { 
    entity: Entity<Anomaly>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

    const actions = entity?.actions
    if (!actions) return null
    
    let componentsActions = actions?.map((action, idx) => {
        switch(action.name) {
            case 'create-anomaly': return !auxAction && (
                    <button key={idx} onClick={() => setAuxAction(action)} className="text-white bg-blue-600 hover:bg-blue-800 rounded-lg px-2">
                        {action.title}
                    </button>
                )
        }
    })

    return (
        <>
            <div className="flex space-x-2">{componentsActions} </div>
            {auxAction?.name === 'create-anomaly' && 
            <InputAnomaly action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
        </>
    )
}

export function Anomalies({ collection, setAction, setPayload }: { 
    collection?: Entity<Collection>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {
    
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

    if (result?.body) collection = getEntityOrUndefined(result.body)
    const anomalies = collection?.entities
    if (!anomalies || !collection) return null

    return (
        <>
            <div className="space-y-3">
                {anomalies.map((entity, idx) => {
                    if (entity.class.includes('anomaly') && entity.rel?.includes('item')) 
                        return <AnomalyItem key={idx} entity={entity} setAction={setAction} setPayload={setPayload}/>
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