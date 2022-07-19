import { useMemo, useState } from "react"
import { Link, Outlet } from "react-router-dom"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Company } from "../models/Models"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { COMPANIES_URL_API } from "../Urls"
import { getEntitiesOrUndefined, getActionsOrUndefined, getPropertiesOrUndefined, getLink, getProblemOrUndefined } from "../models/ModelUtils"
import { InsertCompany } from "./InsertCompany"
import { ActionComponent } from "../components/ActionComponent"

export function ListCompanies() {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const [currentUrl, setCurrentUrl] = useState(COMPANIES_URL_API(1))
    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)
    
    switch (action?.name) {
        case 'create-company': return <ActionComponent action={action} extraInfo={payload} returnComponent={<ListCompanies/>}/>
    }

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    function CompanyItemComponent({entity}: {entity: Entity<Company>}) {
        const company = entity.properties

        const bgColor = company.state === 'active' ? 'bg-white' : 'bg-red-100'
        
        return (
            <div>
                <Link to={`/companies/${company.id}`}>
                    <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 shadow-md hover:bg-gray-200 divide-y space-y-4`}>  
                        <div>
                            <h5 className='mb-2 text-xl tracking-tight text-gray-900'>{company.name}</h5>
                            {/*<p>Number of spaces: {company.numberOfBuildings}</p>*/}
                        </div>
                    </div>
                </Link>
            </div>
        )
    }

    function CompaniesActions({actions}: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        let componentsActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'create-company': return (
                        <button key={idx} onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                            {action.title}
                        </button>
                    )
            }
        })

        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'create-company' && 
                <InsertCompany action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        )
    }

    function Companies({ entities }: { entities?: Entity<Company>[]}) {
        if (!entities) return null
        
        return (
            <div className="space-y-3">
                {entities.map((entity, idx) => {
                    if (entity.class.includes('company') && entity.rel?.includes('item')) {
                        return <CompanyItemComponent key={idx} entity={entity}/>
                    }
                })}
            </div>
        )
    }

    return (
        <div className='px-3 pt-3 space-y-4'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Companies</h1>
            <CompaniesActions actions={getActionsOrUndefined(result?.body)}/>
            <Companies entities={getEntitiesOrUndefined(result?.body)}/>
            <CollectionPagination 
                collection={getPropertiesOrUndefined(result?.body)} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}