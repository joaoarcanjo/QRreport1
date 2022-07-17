import { Link, Navigate, useParams } from 'react-router-dom';
import { FaEdit, FaUserAlt } from 'react-icons/fa';
import { Person } from '../../models/Models';
import { useMemo, useState } from 'react';
import { useFetch } from '../../hooks/useFetch';
import { BASE_URL, PERSONS_URL, PERSON_URL_API } from '../../Urls';
import { ErrorView } from '../../errors/Error';
import * as QRreport from '../../models/QRJsonModel';
import { getEntityOrUndefined, getActionsOrUndefined, getProblemOrUndefined } from '../../models/ModelUtils';
import { ActionComponent } from '../../components/ActionComponent';
import { Action } from '../../models/QRJsonModel';
import { UpdateProfile } from './UpdateProfile';
import { Skills } from './UserSkills';
import { Roles } from './UserRoles';
import { FireAction } from './FireAction';
import { BanAction } from './BanAction';
import { useLoggedInState } from '../Session';
import { Loading } from '../../components/Various';
import { AssignToCompany } from './AssignToCompany';

export function Profile() {
    
    const { personId } = useParams()
    const userSession = useLoggedInState()
    
    //todo: initValues will be the same for all get requests
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<QRreport.Action | undefined>(undefined)
    //Used when any action need to send some payload
    const [auxInfo, setAuxInfo] = useState('')

    const { isFetching, result, error } = useFetch<Person>(PERSON_URL_API(personId), init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    switch (action?.name) {
        case 'delete-user': return <ActionComponent redirectUrl={BASE_URL + PERSONS_URL} action={action}/>
        case 'unban-person': return <ActionComponent action={action}/>
        case 'ban-person': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'rehire-person': return <ActionComponent action={action}/>
        case 'fire-person': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'add-skill': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'remove-skill': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'add-role': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'remove-role': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'assign-to-company': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'update-person': return <UpdateProfile action={action}/>
    }
        
    function UserState({state}: { state: string}) {

        const stateColor = state === 'inactive' ? 'bg-red-600' : 'bg-green-600';
        const stateElement = <span className={`${stateColor} ml-auto py-1 px-2 rounded text-white text-sm`}>{state}</span>
        
        return (
            <li className="flex items-center py-3">
                <span>Status</span>
                {stateElement}
            </li>
        )
    }

    function UserDate({state, time}: {state: string, time: string}) {
        const text = state === 'inactive' ? 'Inactive since' : 'Member since';
        
        return (
            <div className="flex items-center py-3">
                <span>{text}</span> <span className="ml-auto">{`${time}`}</span>
            </div>
        )
    }

    function StateActions({ actions }: {actions?: QRreport.Action[]}) {
        
        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'ban-person': return (
                    <button onClick={() => setAuxAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'unban-person': return (
                    <button onClick={() => setAction(action)} className="w-1/2 bg-yellow-700 hover:bg-yellow-900 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'delete-user': return (
                    <button onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'fire-person': return (
                    <button onClick={() => setAuxAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'rehire-person': return (
                    <button onClick={() => setAction(action)} className="w-1/2 bg-yellow-400 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
                case 'assign-to-company': return (
                    <button onClick={() => setAuxAction(action)} className="w-1/2 bg-green-400 hover:bg-green-600 text-white font-bold py-2 px-4 rounded">
                        {action.title}
                    </button>)
            }
        })

        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'fire-person' && 
                <FireAction action={auxAction} setAction={setAction} setAuxInfo={setAuxInfo} setAuxAction={setAuxAction}/>}
                {auxAction?.name === 'ban-person' && 
                <BanAction action={auxAction} setAction={setAction} setAuxInfo={setAuxInfo} setAuxAction={setAuxAction}/>}
                {auxAction?.name === 'assign-to-company' && 
                <AssignToCompany action={auxAction} setAction={setAction} setPayload={setAuxInfo} setAuxAction={setAuxAction}/>}
            </>
        )
    }

    function UpdateAction({action}: {action: QRreport.Action | undefined}) {
        return action !== undefined ? (
            <button onClick={() => setAction(action)} className="my-1">
                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
            </button>
        ): null
    }   

    function MainInfo({ entity, actions }: { entity: QRreport.Entity<Person> | undefined, actions?: QRreport.Action[] }){
        
        if (!entity) return null
        const person = entity.properties

        return (
            <div className="md:w-5/12 md:mx-2 space-x-4">
                <div className="bg-white p-3 border-t-4 border-blue-900 space-y-3">

                    <div className="image overflow-hidden">
                        <img className="h-auto w-full mx-auto"
                            src="https://media.istockphoto.com/photos/hot-air-balloons-flying-over-the-botan-canyon-in-turkey-picture-id1297349747?b=1&k=20&m=1297349747&s=170667a&w=0&h=oH31fJty_4xWl_JQ4OIQWZKP8C6ji9Mz7L4XmEnbqRU="
                            alt=""/>
                    </div>
                    <div>
                        <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                            <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{person.name.split(' ')[0]}</span>
                            <UpdateAction action={actions?.find(action => action.name === 'update-person')}/> 
                        <div>
                    </div>
                        </div>
                        <ul className="bg-gray-100 text-gray-600 hover:text-gray-700 hover:shadow py-2 px-3 mt-3 divide-y rounded shadow-sm">
                            <UserState state={person.state}/>
                            <UserDate state={person.state} time={`${new Date(person.timestamp).toLocaleDateString()}`}/>
                        </ul>
                    </div>
                    <div>
                        <Link className="w-1/2" to={`tickets/`}>
                            <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                                {person.roles.find(role => role === 'employee') ? 'Work': 'Tickets'}
                            </button>
                        </Link>
                    </div>
                    <StateActions actions={actions}/>
                </div>
            </div>
        )
    }

    function DetailInfo({name, value}: { name: string, value: any}) {
        return (
            <>
                <div className="px-4 py-2 font-semibold">{name}</div>
                <div className="px-4 py-2">{value}</div>
            </>
        )
    }

    function About({ entity }: { entity: QRreport.Entity<Person> | undefined }) {
        if (!entity) return null
        const person = entity.properties
        const isEmployee = person.roles.find(role => role === 'employee') === null

        return (
            <div className="w-full bg-white p-3 shadow-sm rounded-smw-full bg-white p-3 shadow-sm rounded-sm">
                <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                    <FaUserAlt style= {{color: "green"}} /> <span className="tracking-wide">About</span>
                </div>
                <div className="text-gray-700 grid md:grid-cols-2 text-sm">
                    <DetailInfo name={'Name:'} value={person.name}/>
                    <DetailInfo name={'Email:'} value={person.email}/>
                    <DetailInfo name={'Contact No:'} value={person.phone}/>
                    {!isEmployee && <DetailInfo name={'Number of reports:'} value={person.numberOfReports}/>}
                    {!isEmployee && <DetailInfo name={'Reports rejected:'} value={person.reportsRejected}/>}
                    {isEmployee && <DetailInfo name={'Skills:'} value={person.skills?.map(skill => `${skill} ${' '}`)}/>}
                </div>
            </div>
        )
    }

    const entity = getEntityOrUndefined(result?.body)
    return userSession?.isLoggedIn ? (
    <>
        {result?.body?.type === 'problem' || entity === undefined ? <ErrorView /> :
        <div className="mx-auto my-auto p-5">
            <div className="md:flex no-wrap md:-mx-2">
                <MainInfo entity = {entity} actions={getActionsOrUndefined(result?.body)}/>
                <div className="w-full space-y-2">
                    <About entity = {entity}/>
                    {entity.properties.roles.find(role => role === 'employee') &&
                    <Skills entity = {entity} actions={getActionsOrUndefined(result?.body)} setAction={setAction} setAuxInfo={setAuxInfo}/>}
                    <Roles entity = {entity} actions={getActionsOrUndefined(result?.body)} setAction={setAction} setAuxInfo={setAuxInfo}/>
                </div>
            </div>
        </div>}
    </>
    ) : <Navigate to={'/'}></Navigate>
}