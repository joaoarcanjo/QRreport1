import { Link } from "react-router-dom"
import { PersonItem } from "../models/Models"
import * as QRreport from '../models/QRJsonModel'
import { ProblemJson } from "../models/ProblemJson"
import { useMemo, useState } from "react"
import { Collection } from "../pagination/CollectionPagination"
import { PERSONS_URL_API } from "../Urls"
import { ErrorView } from "../errors/Error"
import SignupForm from "./signup/SignupForm"
import { getActionsOrUndefined, getEntitiesOrUndefined, getProblemOrUndefined } from "../models/ModelUtils"
import { useFetch } from "../hooks/useFetch"
import { Loading } from "../components/Various"

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
    const [direction, setDirection] = useState('desc')
    const [sortBy, setSortBy] = useState('date')
    const [isAction, setAction] = useState(false)
    
    //const [currentPage, setPage] = useState(0)
    const { isFetching, result, error } = useFetch<Collection>(PERSONS_URL_API(sortBy, direction), init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView message="Unexpected error"/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    if (isAction) return <SignupForm/>
    
    return (
        <div className='px-3 pt-3 space-y-4'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Persons</h1>
            {isFetching ? <Loading/> : 
            <>
                <ProblemComponent problem={getProblemOrUndefined(result?.body)}/>
                <PersonsList entities={getEntitiesOrUndefined(result?.body)}/>
                <PersonsActions actions={getActionsOrUndefined(result?.body)} setAction={setAction}/>
            </>}
        </div>
    )
}