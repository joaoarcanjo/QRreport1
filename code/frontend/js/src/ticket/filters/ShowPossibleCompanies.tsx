import { useState, useMemo } from "react"
import { Outlet } from "react-router-dom"
import { CloseButton, Loading } from "../../components/Various"
import { useFetch } from "../../hooks/useFetch"
import { Company } from "../../models/Models"
import { getProblemOrUndefined, getEntityOrUndefined, getPropertiesOrUndefined, getLink } from "../../models/ModelUtils"
import { Entity } from "../../models/QRJsonModel"
import { Collection, CollectionPagination } from "../../pagination/CollectionPagination"
import { COMPANIES_URL_API } from "../../Urls"

export function ShowPossibleCompanies({setPayload, currentCompany}: {
    currentCompany: any
    setPayload: React.Dispatch<React.SetStateAction<any>>
}) {
    
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const [currentUrl, setCurrentUrl] = useState('')

    const init = useMemo(() => initValues ,[])

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if(currentUrl === '') {
        setCurrentUrl(COMPANIES_URL_API)
    }
 
    if (isFetching) return <Loading/>
    //if (error) return <ErrorView error={error}/>
    
    const problem = getProblemOrUndefined(result?.body)
    //if (problem) return <ErrorView problemJson={problem}/>

    function CompanyItem({entity}: {entity: Entity<Company>}) {
        if (!entity) return null;
        const company = entity.properties
        
        return (
            <div className='flex p-1 bg-white rounded-lg border border-gray-400'> 
                <div className='w-full flex space-x-4'>
                    <h5 className='font-md text-gray-900'>{company.name}</h5>
                </div>
                <div className='w-full flex justify-end' >
                    <button 
                        className='px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'
                        onClick= {() => setPayload(company)}>
                        Select
                    </button>
                </div>
            </div>
        )
    }

    function Companies({entity}: { entity: Entity<Collection> | undefined }) {

        if(!entity) return null;

        return (
            <div className='flex flex-col space-y-3'>
                {entity.entities!!.map((company, idx) => <CompanyItem key={idx} entity={company}/>)}
            </div>
        )
    }

    return (
        <div className="space-y-1 p-3 bg-white rounded-lg border border-gray-200">
        <p>Selected company: {currentCompany?.name}</p>
            <Companies entity={getEntityOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}