import { Link, Navigate, Outlet } from "react-router-dom"
import { PersonItem } from "../models/Models"
import * as QRreport from '../models/QRJsonModel'
import { useMemo, useState } from "react"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { LOGIN_URL, PERSONS_URL_API } from "../Urls"
import { ErrorView } from "../errors/Error"
import { getEntitiesOrUndefined, getLink, getProblemOrUndefined, getPropertiesOrUndefined, getActionsOrUndefined } from "../models/ModelUtils"
import { useFetch } from "../hooks/useFetch"
import { Loading } from "../components/Various"
import { useLoggedInState } from "./Session"
import { Action } from "../models/QRJsonModel"
import { InputUser } from "./InputUser"
import { ActionComponent } from "../components/ActionComponent"

function PersonItemComponent({ entity }: { entity: QRreport.Entity<PersonItem> }) {
    const person = entity.properties
    return (
        <div>
            <Link to={`/persons/${person.id}`}>
                <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100'>  
                    <h5 className='mb-2 text-xl font-bold tracking-tight text-gray-900'>{person.name}</h5>
                    <p>{person.email}</p>
                </div>
            </Link>
        </div>
    )
}

function PersonsList({ entities }: { entities?: QRreport.Entity<PersonItem>[]}) {
    if (!entities) return null
    return (
    <>
        {entities.map((entity, idx) => {
            if (entity.class.includes('person') && entity.rel?.includes('item')) {
                return <PersonItemComponent key={idx} entity={entity}/>
            }
        })}
    </>)
}

export function ListPersons() {
    const credentials: RequestInit = {
        credentials: "include",
        headers: { 
            'Request-Origin': 'WebApp'
        }
    }
    const init = useMemo(() => credentials , [])
    const [currentUrl, setCurrentUrl] = useState('')
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const userSession = useLoggedInState()
    
    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    switch (action?.name) {
        case 'create-person': return <ActionComponent action={action} extraInfo={payload} returnComponent={<ListPersons/>} />
    }

    if(userSession?.isLoggedIn && currentUrl === '') 
        setCurrentUrl(PERSONS_URL_API)
    else if(!userSession?.isLoggedIn) 
        return <Navigate to={LOGIN_URL}/>

    if (isFetching) return <Loading/>
    if (error) return <ErrorView message="Unexpected error"/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    function PersonsActions({actions}: {actions: QRreport.Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<QRreport.Action | undefined>(undefined)

        let componentsActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'create-person': return (
                        <button key={idx} onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                            {action.title}
                        </button>
                    )
            }
        })

        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'create-person' && 
                <InputUser action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        )
    }
    
    return (
        <div className='px-3 pt-3 space-y-2'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Persons</h1>
            <PersonsActions actions={getActionsOrUndefined(result?.body)}/>
            <PersonsList entities={getEntitiesOrUndefined(result?.body)}/>
            <CollectionPagination 
                collection={getPropertiesOrUndefined(result?.body)} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}