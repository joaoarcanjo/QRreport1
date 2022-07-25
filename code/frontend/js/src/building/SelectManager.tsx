import { useMemo, useState } from "react"
import { AiFillCloseCircle } from "react-icons/ai"
import { Outlet } from "react-router-dom"
import { ErrorPopup } from "../components/ErrorPopup"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Person } from "../models/Models"
import { getProblemOrUndefined, getEntityOrUndefined, getPropertiesOrUndefined, getLink } from "../models/ModelUtils"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { BASE_URL_API } from "../Urls"

export function SelectManager({action, setAction, setPayload, setAuxAction, currentManager}: {
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<any>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    currentManager?: number,
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const property = action.properties.find(prop => {if(prop.name === 'managerId'){ return prop}})
    const href = property?.possibleValues?.href
    const url = href === undefined || null ? '' : BASE_URL_API + href

    const init = useMemo(() => initValues ,[])
    const [currentUrl, setCurrentUrl] = useState(url)

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorPopup error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>


    function ManagerItem({entity}: {entity: Entity<Person>}) {
        if (!entity) return null;
        const person = entity.properties
        const isManager = person.id === currentManager
        
        return (
            <div className='flex p-5 bg-white rounded-lg border border-gray-200 shadow-md'>  
                <div className='w-full flex space-x-4'>
                    <h5 className='font-md text-gray-900'>{person.name}</h5>
                </div>
                <div className='w-full flex justify-end' >
                    {isManager ? null : <button
                        className='px-3 py-2 text-xs font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'
                        onClick= {() => { setAction && setAction(action); setPayload(person)}}>
                        Select
                    </button>}
                </div>

            </div>
        )
    }

    function Managers({entity}: { entity: Entity<Collection> | undefined }) {

        if(!entity) return null;

        return (
            <div className='flex flex-col space-y-3'>
                {entity.entities!!.map((manager, idx) => <ManagerItem key={idx} entity={manager}/>)}
            </div>
        )
    }

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200 shadow-md">
            {setAuxAction && 
            <button onClick={() => setAuxAction(undefined)}>
                <AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
            </button>}
            <Managers entity={getEntityOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}