import React from "react"
import { useState, useMemo } from "react"
import { Outlet } from "react-router-dom"
import { Loading } from "../../components/Various"
import { useFetch } from "../../hooks/useFetch"
import { EmployeeState } from "../../models/Models"
import { getProblemOrUndefined, getEntityOrUndefined, getPropertiesOrUndefined, getLink } from "../../models/ModelUtils"
import { Entity } from "../../models/QRJsonModel"
import { Collection, CollectionPagination } from "../../pagination/CollectionPagination"
import { TICKETS_EMPLOYEE_STATES_URL_API } from "../../Urls"


export function ShowEmployeeStates({currentState, setPayload}: {
    currentState: any, 
    setPayload: React.Dispatch<React.SetStateAction<any>>,
}) {
    
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const [currentUrl, setCurrentUrl] = useState('')

    const init = useMemo(() => initValues ,[])

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if(currentUrl === '') {
        setCurrentUrl(TICKETS_EMPLOYEE_STATES_URL_API)
    }
 
    if (isFetching) return <Loading/>
    //if (error) return <ErrorView error={error}/>
    
    const problem = getProblemOrUndefined(result?.body)
    //if (problem) return <ErrorView problemJson={problem}/>

    function StateItem({entity}: {entity: Entity<EmployeeState>}) {
        if (!entity) return null;
        const state = entity.properties
        
        return (
            <div className='flex p-1 bg-white rounded-lg border border-gray-400'>  
                <div className='w-full flex space-x-4'>
                    <h5 className='font-md text-gray-900'>{state.name}</h5>
                </div>
                <div className='w-full flex justify-end' >
                    <button 
                        className='px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'
                        onClick= {() => {setPayload(state)}}>
                        Select
                    </button>
                </div>

            </div>
        )
    }

    function States({entity}: { entity: Entity<Collection> | undefined }) {

        if(!entity) return null;

        return (
            <div className='flex flex-col space-y-3'>
                {entity.entities!!.map((company, idx) => <StateItem key={idx} entity={company}/>)}
            </div>
        )
    }

    return (
        <div className="space-y-1 p-3 bg-white rounded-lg border border-gray-200">
         <p>Selected state: {currentState?.name}</p>
            <States entity={getEntityOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}

