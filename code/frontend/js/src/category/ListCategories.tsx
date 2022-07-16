import { useMemo, useState } from "react"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { useFetch } from "../hooks/useFetch"
import { Category } from "../models/Models"
import { Action, Entity } from "../models/QRJsonModel"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { CATEGORIES_URL_API } from "../Urls"
import { ActionComponent } from "../components/ActionComponent"
import { InputCategory } from "./InputCategory"
import { getEntitiesOrUndefined, getActionsOrUndefined, getPropertiesOrUndefined, getLink, getProblemOrUndefined } from "../models/ModelUtils"
import { MdExpandMore, MdExpandLess, MdDelete } from "react-icons/md"
import { GrUpdate } from "react-icons/gr"
import { Outlet } from "react-router-dom"

export function ListCategories() {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const [currentUrl, setCurrentUrl] = useState(CATEGORIES_URL_API(1))

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    switch (action?.name) {
        case 'create-category': return <ActionComponent action={action} extraInfo={payload} returnComponent={<ListCategories/>} />
        case 'update-category': return <ActionComponent action={action} extraInfo={payload} returnComponent={<ListCategories/>} />
        case 'activate-category': return <ActionComponent action={action} returnComponent={<ListCategories/>} />
        case 'deactivate-category': return <ActionComponent action={action} returnComponent={<ListCategories/>} />
    } 

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error} />

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    function CategoryItemComponent({entity}: {entity: Entity<Category>}) {
        const category = entity.properties

        const bgColor = category.state === 'active' ? 'bg-white' : 'bg-red-100'
        
        const [moreInfo, showMoreInfo] = useState(false)
        
        return (
        <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 divide-y space-y-4`}> 
            <div className="items-center">
                <div><span className='text-xl font-md text-gray-900'>{category.name}</span></div>
            </div>
            <div>
                <button className='my-1' onClick={() => showMoreInfo(!moreInfo)}>
                    {!moreInfo && <MdExpandMore style= {{ color: 'blue', fontSize: '2em' }} />}
                    { moreInfo && <MdExpandLess style= {{ color: 'blue', fontSize: '2em' }} />}
                </button>
                <div className='space-y-4'>{moreInfo && <CategoryActions actions={entity.actions}/>}</div>
            </div>
        </div>)
    }
    
    function CategoryActions({actions}: {actions: Action[] | undefined}) {
    
        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)
    
        if(!actions) return null 
        
        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'update-category': return !auxAction && (
                        <button onClick={() => setAuxAction(action)} className="text-white bg-yellow-400 hover:bg-yellow-600 rounded-lg px-2">
                            <GrUpdate/>
                        </button>
                    )
                case 'activate-category': return !auxAction && (
                    <button onClick={() => setAction(action)} className="bg-green-700 hover:bg-green-900 text-white font-bold py-2 px-2 rounded">
                        {action.title}
                    </button>
                )
                case 'deactivate-category': return !auxAction && (
                    <button onClick={() => setAction(action)} className="bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-2 rounded">
                        {action.title}
                    </button>
                )
            }
        })
    
        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'update-category' && 
                <InputCategory action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        )
    }

    function CategoriesActions({actions}: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'create-category': return (
                        <button onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                            {action.title}
                        </button>
                    )
            }
        })

        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'create-category' && 
                <InputCategory action={auxAction} setAction={setAction} setAuxAction={setAuxAction} setPayload={setPayload}/>}
            </>
        )
    }

    function Categories({ entities }: { entities?: Entity<Category>[]}) {
        if (!entities) return null
        
        return (
            <div className="space-y-3">
                {entities.map((entity, idx) => {
                    if (entity.class.includes('category') && entity.rel?.includes('item')) {
                        return <CategoryItemComponent key={idx} entity={entity}/>
                    }
                })}
            </div>
        )
    }

    return (
        <div className='px-3 pt-3 space-y-4'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Categories</h1>
            <CategoriesActions actions={getActionsOrUndefined(result?.body)}/>
            <Categories entities={getEntitiesOrUndefined(result?.body)}/>
            <CollectionPagination collection={getPropertiesOrUndefined(result?.body)} setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}