import { useMemo, useState } from "react"
import { Outlet } from "react-router-dom"
import { Loading, CloseButton } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Category } from "../models/Models"
import { getProblemOrUndefined, getEntityOrUndefined, getPropertiesOrUndefined, getLink } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { BASE_URL_API } from "../Urls"

export function SelectCategory({action, propName, setPayload, setAuxAction}: {
    action: Action, propName: string,
    setPayload: React.Dispatch<React.SetStateAction<any>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const property = action.properties.find(prop => {if(prop.name === propName){ return prop}})
    const href = property?.possibleValues?.href
    const url = href === undefined || null ? '' : BASE_URL_API + href

    const init = useMemo(() => initValues ,[])
    const [currentUrl, setCurrentUrl] = useState(url)

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>


    function CategoryItem({entity}: {entity: Entity<Category>}) {
        if (!entity) return null;
        const category = entity.properties
        const state = category.state
        const stateColor = state === 'active' ? 'bg-white' : 'bg-red-200'
        return (
            <div className={`flex p-1 ${stateColor} rounded-lg border border-gray-400`}>  
                <div className='w-full flex space-x-4'>
                    <h5 className='font-md text-gray-900'>{category.name}</h5>
                </div>
                {state === 'active' &&
                <div className='w-full flex justify-end' >
                    <button 
                        className='px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'
                        onClick= {() => setPayload(category)}>
                        Select
                    </button>
                </div>}
            </div>
        )
    }

    function Companies({entity}: { entity: Entity<Collection> | undefined }) {

        if(!entity) return null;

        return (
            <div className='flex flex-col space-y-3'>
                {entity.entities!!.map((category, idx) => <CategoryItem key={idx} entity={category}/>)}
            </div>
        )
    }

    return (
        <div className="space-y-1 p-3 bg-white rounded-lg border border-gray-200 shadow-md">
            {setAuxAction && <CloseButton onClickHandler={() => setAuxAction(undefined) }/>}
            <Companies entity={getEntityOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}