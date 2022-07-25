import { useMemo, useState } from "react"
import { Link, Navigate, Outlet } from "react-router-dom"
import { MdExpandMore, MdExpandLess } from "react-icons/md"
import { TicketItem } from "../models/Models"
import { useLoggedInState, USER_ROLE } from "../user/Session"
import { useFetch } from "../hooks/useFetch"
import { Collection, CollectionPagination } from "../pagination/CollectionPagination"
import { LOGIN_URL, TICKETS_URL_API } from "../Urls"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { Entity } from "../models/QRJsonModel"
import { getEntitiesOrUndefined, getLink, getProblemOrUndefined, getPropertiesOrUndefined } from "../models/ModelUtils"
import { Filters } from "./filters/Filters"

export function ListTickets() {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const [currentUrl, setCurrentUrl] = useState('')
    const userSession = useLoggedInState()

    const init = useMemo(() => initValues ,[])

    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)

    if(userSession?.isLoggedIn && currentUrl === '') 
        setCurrentUrl(TICKETS_URL_API)
    else if(!userSession?.isLoggedIn) 
        return <Navigate to={LOGIN_URL}/>

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    function TicketItemComponent({entity}: {entity: Entity<TicketItem>}) {
        const ticket = entity.properties

        const [moreInfo, showMoreInfo] = useState<boolean>(false)

        const desc = ticket.description === undefined ? 'No description' : ticket.description

        return (
            <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100 divide-y space-y-4'>  
                <div>
                    <Link to={`/tickets/${ticket.id}`}>
                        <h5 className='mb-2 text-xl tracking-tight text-gray-900'>{ticket.subject}</h5>
                    </Link>
                    <div>
                        <span className='p-1 bg-blue-400 text-white rounded-lg shadow-md px-1'>
                            {useLoggedInState()?.userRole === USER_ROLE ? ticket.userState : ticket.employeeState} 
                        </span>
                        <div className='flex justify-end space-x-1 text-blue-700 text-sm'>
                            <p>{`${ticket.company} | `}</p>
                            <p>{`${ticket.building} | `}</p>
                            <p>{ticket.room}</p>
                        </div>
                    </div>
                </div>
                <div>
                    <button className='my-1' onClick={() => showMoreInfo(!moreInfo)}>
                        {!moreInfo && <MdExpandMore style= {{ color: 'blue', fontSize: '2em' }} />}
                        { moreInfo && <MdExpandLess style= {{ color: 'blue', fontSize: '2em' }} />}
                    </button>
                    <div className='space-y-4'>
                        {moreInfo && <p className='text-sm text-gray-800'>{desc}</p>}
                    </div>
                </div>
            </div>
        )
    }

    function ListTickets({ entities }: { entities?: Entity<TicketItem>[]}) {
        if (!entities) return null
        return (
            <>
                {entities.map((entity, idx) => {
                    if (entity.class.includes('ticket') && entity.rel?.includes('item')) {
                        return <TicketItemComponent key={idx} entity={entity}/>
                    }
                })}
            </>
        )
    }

    return (
        <div className='px-3 pt-3 space-y-4'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Tickets</h1>
            <Filters currentUrl={currentUrl} setCurrentUrl={setCurrentUrl}/>
            <ListTickets entities={getEntitiesOrUndefined(result?.body)}/>
            <CollectionPagination 
                collection={getPropertiesOrUndefined(result?.body)} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getLink('pagination', result?.body)}/>
            <Outlet/>
        </div>
    )
}