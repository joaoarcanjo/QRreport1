import { Link } from "react-router-dom"
import { PersonItem } from "../Models"
import * as QRreport from '../models/QRJsonModel'
import { ProblemJson } from "../models/ProblemJson"
import { useMemo, useState } from "react"
import { Collection } from "../pagination/CollectionPagination"
import { PERSONS_URL } from "../Urls"
import { DisplayError } from "../Error"
import SignupForm from "./signup/SignupForm"
import { getActionsOrUndefined, getEntitiesOrUndefined, getProblemOrUndefined } from "../models/ModelUtils"
import { useFetch } from "../hooks/useFetch"

function PersonItemComponent({ entity }: { entity: QRreport.Entity<PersonItem> }) {
    const person = entity.properties
    return (
        <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100'>  
            <Link to={`/persons/${person.id}`}>
                <h5 className='mb-2 text-xl font-bold tracking-tight text-gray-900'>{person.name}</h5>
            </Link>
            <p>{person.email}</p>
        </div>
    )
}

function PersonsList({ entities }: { entities?: QRreport.Entity<PersonItem>[]}) {
    if (!entities) return null
    return (
    <>
        {entities.map((entity, idx) => {
            console.log(entity)
            if (entity.class.includes('person') && entity.rel?.includes('item')) {
                return <PersonItemComponent key={idx} entity={entity}/>
            }
        })}
    </>)
}

function PersonsActions({ actions, setAction }: { actions?: QRreport.Action[], setAction: React.Dispatch<React.SetStateAction<boolean>> }) {
    function onClickHandler() {
        setAction(true)
    }
    const actionsElements = actions?.map((action, idx) => {
        switch (action.name) {
            case 'create-person': return (
                <div key={idx} className='mb-2 ml-8'>
                    <button onClick={onClickHandler} className='rounded-lg bg-sky-400 p-2 text-white font-bold'>
                        Create Project
                    </button>
                </div>
            )
        }
    })
    return actionsElements?.length === 0 ? null : <div>{actionsElements}</div>
}

function ProblemComponent({ problem }: { problem?: ProblemJson }) {
    if (!problem) return null
    return (
        <div>
            {Object.entries(problem).map((key, value) => <p>{`${key[0]}: ${key[1]}`}</p>)}
        </div>
    )
}

export function ListPersons() {
    const credentials: RequestInit = {
        credentials: "include",
        headers: { 
            'Request-Origin': 'WebApp'
        }
    }
    const init = useMemo(() => credentials ,[])
    
    //const [currentPage, setPage] = useState(0)
    const { isFetching, isCanceled, cancel, result, error } = useFetch<Collection>(PERSONS_URL/* + currentPage*/, init)

    const [isAction, setAction] = useState(false)

    if (isCanceled) return <p>Canceled</p>
    if (error !== undefined) {  
        return <DisplayError message="Unexpected error"/>
    }

    if (isAction) return <SignupForm /*action={getAction("create-project", result?.body)!!} setAction={setAction}*//>
    
    
    return (
        <div className='px-3 pt-3 space-y-4'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Persons</h1>
            {isFetching ? <p>Fetching...</p> : 
            <>
                <ProblemComponent problem={getProblemOrUndefined(result?.body)}/>
                <PersonsList entities={getEntitiesOrUndefined(result?.body)}/>
                <PersonsActions actions={getActionsOrUndefined(result?.body)} setAction={setAction}/>
            </>}
        </div>
    )
}