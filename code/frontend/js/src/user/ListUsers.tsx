import { Link, Navigate } from "react-router-dom"
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
            <Link to={`/profile/${person.id}`}>
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
    const init = useMemo(() => credentials ,[])
    const [direction, setDirection] = useState('desc')
    const [sortBy, setSortBy] = useState('date')
    
    //const [currentPage, setPage] = useState(0)
    const { isFetching, result, error } = useFetch<Collection>(PERSONS_URL_API(sortBy, direction), init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView message="Unexpected error"/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    return (
        <div className='px-3 pt-3 space-y-2'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Persons</h1>
            <PersonsList entities={getEntitiesOrUndefined(result?.body)}/>
        </div>
    )
}