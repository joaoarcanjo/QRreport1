import { useState, useMemo } from "react"
import { Outlet } from "react-router-dom"
import { CloseButton, Loading } from "../../components/Various"
import { ErrorView } from "../../errors/Error"
import { useFetch } from "../../hooks/useFetch"
import { Company, Person } from "../../models/Models"
import { getProblemOrUndefined, getEntityOrUndefined, getPropertiesOrUndefined, getLink } from "../../models/ModelUtils"
import { Action, Entity } from "../../models/QRJsonModel"
import { Collection, CollectionPagination } from "../../pagination/CollectionPagination"
import { BASE_URL_API } from "../../Urls"

export function SelectUserCompany({action, setAction, setPayload, setAuxAction}: {
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<any>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const property = action.properties.find(prop => {if(prop.name === 'company'){ return prop}})
    const href = property?.possibleValues?.href
    const url = href === undefined || null ? '' : BASE_URL_API + href

    const init = useMemo(() => initValues ,[])
    const [currentUrl, setCurrentUrl] = useState(url)

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>


    function CompanyItem({entity}: {entity: Entity<Company>}) {
        if (!entity) return null;
        const company = entity.properties
        
        return (
            <div className='flex p-5 bg-white rounded-lg border border-gray-200 shadow-md'>  
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
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200 shadow-md">
            {setAuxAction && <CloseButton onClickHandler={() => setAuxAction(undefined) }/>}
            <Companies entity={getEntityOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}