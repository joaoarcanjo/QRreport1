import { useMemo, useState } from "react"
import { Link, useParams } from "react-router-dom"
import { MdExpandMore, MdExpandLess, MdFilterList } from "react-icons/md"
import { TbArrowBigTop, TbArrowBigDown } from "react-icons/tb"
import { TicketItem } from "../models/Models"
import { useLoggedInState } from "../user/Session"
import { useFetch } from "../hooks/useFetch"
import { Collection } from "../pagination/CollectionPagination"
import { TICKETS_URL_API } from "../Urls"
import { Loading } from "../components/Various"
import { ErrorView } from "../errors/Error"
import { Entity } from "../models/QRJsonModel"
import { getEntitiesOrUndefined, getProblemOrUndefined } from "../models/ModelUtils"

export function ListTickets() {

    const [direction, setDirection] = useState('desc')
    const [sortBy, setSortBy] = useState('date')

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])

    const { isFetching, result, error } = useFetch<Collection>(TICKETS_URL_API(sortBy, direction), init)
    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>
    
    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    function Filters() {

        const [directionAux, setDirectionAux] = useState(direction)
        const [sortByAux, setSortByAux] = useState(sortBy)

        return (
            <div className='flex w-full gap-4'>
                <select className='border rounded-lg' onChange={value => setSortByAux(value.target.value)}>
                    <option value='date'>Date</option>
                    <option value='name'>Name</option>
                </select>       
                <button 
                    className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-5 h-12 inline-flex items-center'
                    onClick= {() => { setDirectionAux(directionAux === 'desc' ? 'asc' : 'desc') }}>
                    {directionAux === 'asc' && <TbArrowBigTop style= {{ color: 'white', fontSize: '2em' }} />}
                    {directionAux === 'desc' && <TbArrowBigDown style= {{ color: 'white', fontSize: '2em' }} />}
                </button>     
                <button className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-5 h-12 inline-flex items-center'
                        onClick= {() => {setDirection(directionAux); setSortBy(sortByAux) }}>
                    <MdFilterList style= {{ color: 'white', fontSize: '2em' }} />
                </button>
            </div>    
        )
    }

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
                    <div className=' p-1 bg-amber-100 rounded-lg shadow-md'>
                        {useLoggedInState()?.userRole === 'user' ? ticket.userState : ticket.employeeState} 
                    </div>
                </div>
                <div>
                    <button className='my-1' onClick={() => showMoreInfo(!moreInfo)}>
                        {!moreInfo && <MdExpandMore style= {{ color: 'blue', fontSize: '2em' }} />}
                        { moreInfo && <MdExpandLess style= {{ color: 'blue', fontSize: '2em' }} />}
                    </button>
                    <div className='space-y-4'>
                        {moreInfo && <p className='text-sm text-gray-800'>{desc}</p>}
                        {moreInfo && 
                        <div className='flex justify-end'>
                            <button className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-2.5 py-1 inline-flex items-center mr-2 mb-2'> 
                                Edit
                            </button>
                        </div>}
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
            <Filters/>
            <ListTickets entities={getEntitiesOrUndefined(result?.body)}/>
        </div>
    )
}