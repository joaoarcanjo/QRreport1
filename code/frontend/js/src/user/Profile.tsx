import { Link, useParams } from 'react-router-dom';
import { FaEdit, FaUserAlt } from 'react-icons/fa';
import { Person } from '../Models';
import { useMemo, useState } from 'react';
import { useFetch } from '../hooks/useFetch';
import { PERSON_URL } from '../Urls';
import { DisplayError } from '../Error';
import * as QRreport from '../models/QRJsonModel';
import { getAction, getEntityOrUndefined, getActionsOrUndefined } from '../models/ModelUtils';
import { DeleteComponent } from '../components/DeleteComponent';
import { UpdateProfile } from './UpdateProfile';


/* Main user info section */
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

type UserDateProps = {
    state: string,
    time: Date,
}

function UserDate({state, time}: UserDateProps) {
    const text = state === 'inactive' ? 'Inactive since' : 'Member since';
    
    return (
        <div className="flex items-center py-3">
            <span>{text}</span>
            <span className="ml-auto">{`${time}`}</span>
        </div>
    )
}

function MainInfo({ entity, actions, setAction, personId }: { 
    entity: QRreport.Entity<Person> | undefined, 
    actions?: QRreport.Action[], 
    setAction: React.Dispatch<React.SetStateAction<string>> ,
    personId: string | undefined
}){
    if (!entity) return null
    const person = entity.properties

    return (
        <div className="md:w-3/12 md:mx-2">
            <div className="bg-white p-3 border-t-4 border-blue-900 space-y-3">

                <div className="image overflow-hidden">
                    <img className="h-auto w-full mx-auto"
                        src="https://media.istockphoto.com/photos/hot-air-balloons-flying-over-the-botan-canyon-in-turkey-picture-id1297349747?b=1&k=20&m=1297349747&s=170667a&w=0&h=oH31fJty_4xWl_JQ4OIQWZKP8C6ji9Mz7L4XmEnbqRU="
                        alt=""/>
                </div>

                <div>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{person.name.split(' ')[0]}</span>

                        {actions?.find(action => action.name === 'update-person') && 
                        <button onClick={() => setAction('update-person')} className="my-1">
                            <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                        </button>}

                    </div>
                    <ul className="bg-gray-100 text-gray-600 hover:text-gray-700 hover:shadow py-2 px-3 mt-3 divide-y rounded shadow-sm">
                        <UserState state={person.state}/>
                        <UserDate state={person.state} time={person.timestamp}/>
                    </ul>
                </div>

                <div className="flex space-x-4">
                    <Link className="w-1/2" to={`/persons/${personId}/tickets/`}>
                        <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                            {person.roles.find(role => role.name === 'employee') ? 'Work': 'Tickets'}
                        </button>
                    </Link>

                    {actions?.find(action => action.name === 'ban-person') &&
                    <button onClick={() => setAction('ban-person')} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        Ban
                    </button>}

                    {actions?.find(action => action.name === 'delete-user') &&
                    <button onClick={() => setAction('delete-user')} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        Delete
                    </button>}
                </div>
            </div>
        </div>
    )
}

type DetailInfoProp = {
    name: string,
    value: any,
}

function DetailInfo({name, value}: DetailInfoProp) {
    return (
        <div>
            <div className="px-4 py-2 font-semibold">{name}</div>
            <div className="px-4 py-2">{value}</div>
        </div>
    )
}

function About({ entity }: { entity: QRreport.Entity<Person> | undefined }) {
    if (!entity) return null
    const person = entity.properties
    const isEmployee = person.roles.find(role => role.name === 'employee') === null

    return (
        <div className="w-full md:w-9/12 md:mx-2">
            <div className="bg-white p-3 shadow-sm rounded-sm">
                <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                    <FaUserAlt style= {{color: "green"}} /> <span className="tracking-wide">About</span>
                </div>
                <div className="text-gray-700">
                    <div className="grid md:grid-cols-2 text-sm">
                        <DetailInfo name={'Name:'} value={person.name}/>
                        <DetailInfo name={'Email:'} value={person.email}/>
                        <DetailInfo name={'Contact No:'} value={person.phone}/>
                        {!isEmployee && <DetailInfo name={'Number of reports:'} value={person.numberOfReports}/>}
                        {!isEmployee && <DetailInfo name={'Reports rejected:'} value={person.reportsRejected}/>}
                        {isEmployee && <DetailInfo name={'Skills:'} value={person.skills?.map(skill => `${skill.name} ${' '}`)}/>}
                    </div>
                </div>
            </div>
        </div>
    )
}

export function Profile() {
    
    const { personId } = useParams()

    //todo: credentials will be the same for all get requests
    const credentials: RequestInit = {
        credentials: "include",
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => credentials ,[])
    const [action, setAction] = useState('')

    const { isFetching, isCanceled, cancel, result, error } = useFetch<Person>(PERSON_URL(personId), init)

    if (isFetching) return <p>Fetching...</p>
    if (isCanceled) return <p>Canceled</p>
    if (error !== undefined) {
        console.log(error)
        return <DisplayError error={error}/>
    }

    switch (action) {
        case 'delete-person': return <DeleteComponent urlToDelete={PERSON_URL(personId)} redirectUrl='/persons' setAction={setAction}/>
        case 'ban-person': return <></>
        case 'update-person': return <UpdateProfile action={getAction('update-person', result?.body)!!} setAction={setAction} />
    }

    const entity = getEntityOrUndefined(result?.body)
    return (
    <>
        {result?.body?.type === 'problem' || entity === undefined ? <DisplayError/> :
        <div className="container mx-auto my-5 p-5">
            <div className="md:flex no-wrap md:-mx-2">
                <MainInfo entity = {entity} actions={getActionsOrUndefined(result?.body)} setAction={setAction} personId={personId}/>
                <About entity = {entity}/>
            </div>
        </div>}
    </>
    )
}