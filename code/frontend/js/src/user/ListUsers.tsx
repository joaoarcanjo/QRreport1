import { Link, Navigate, Outlet } from "react-router-dom"
import { PersonItem } from "../models/Models"
import * as QRreport from '../models/QRJsonModel'
import { useMemo, useState } from "react"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { LOGIN_URL, PERSONS_URL_API } from "../Urls"
import { ErrorView } from "../errors/Error"
import { getEntitiesOrUndefined, getLink, getProblemOrUndefined, getPropertiesOrUndefined } from "../models/ModelUtils"
import { useFetch } from "../hooks/useFetch"
import { Loading } from "../components/Various"
import { useLoggedInState } from "./Session"

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
    const init = useMemo(() => credentials ,[])
    const [direction, setDirection] = useState('desc')
    const [sortBy, setSortBy] = useState('date')
    const [currentUrl, setCurrentUrl] = useState('')
    const userSession = useLoggedInState()
    
    //const [currentPage, setPage] = useState(0)
    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if(userSession?.isLoggedIn && currentUrl === '') 
        setCurrentUrl(PERSONS_URL_API)
    else if(!userSession?.isLoggedIn) 
        return <Navigate to={LOGIN_URL}/>

    if (isFetching) return <Loading/>
    if (error) return <ErrorView message="Unexpected error"/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    return (
        <div className='px-3 pt-3 space-y-2'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Persons</h1>
            <PersonsList entities={getEntitiesOrUndefined(result?.body)}/>
            <CollectionPagination 
                collection={getPropertiesOrUndefined(result?.body)} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}