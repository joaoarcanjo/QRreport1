import { Link, useParams } from 'react-router-dom';
import { FaEdit, FaUserAlt } from 'react-icons/fa';
import { CategoryItem, Person } from '../Models';
import { useMemo, useState } from 'react';
import { useFetch } from '../hooks/useFetch';
import { BASE_URL, BASE_URL_API, PERSONS_URL, PERSONS_URL_API, PERSON_URL, PERSON_URL_API } from '../Urls';
import { DisplayError } from '../Error';
import * as QRreport from '../models/QRJsonModel';
import { getEntityOrUndefined, getActionsOrUndefined, getEntitiesOrUndefined } from '../models/ModelUtils';
import { MdAddCircleOutline, MdOutlineAssignmentInd, MdRemoveCircleOutline, MdWork } from 'react-icons/md';
import { ActionComponent } from './UserAction';
import { Action } from '../models/QRJsonModel';
import { UpdateProfile } from './UpdateProfile';
import { Collection } from '../pagination/CollectionPagination';
import { Loading } from '../components/Various';

export function Profile() {
    
    const { personId } = useParams()

    //todo: initValues will be the same for all get requests
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<QRreport.Action | undefined>(undefined)
    const [auxInfo, setAuxInfo] = useState('')

    const { isFetching, isCanceled, cancel, result, error } = useFetch<Person>(BASE_URL_API + PERSON_URL_API(personId), init)

    if (isFetching) return <p>Fetching...</p>
    if (isCanceled) return <p>Canceled</p>
    if (error !== undefined) {
        console.log(error)
        return <DisplayError error={error}/>
    }
    
    switch (action?.name) {
        case 'delete-user': return <ActionComponent redirectUrl={BASE_URL + PERSONS_URL} action={action}/>
        case 'unban-person': return <ActionComponent action={action}/>
        case 'ban-person': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'rehire-person': return <ActionComponent action={action}/>
        case 'fire-person': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'add-skill': return <ActionComponent action={action} extraInfo={auxInfo}/>
        case 'remove-skill': return <ActionComponent action={action}/>
        case 'update-person': return <UpdateProfile action={action}/>
    }
        
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

    function UserDate({state, time}: {state: string, time: Date}) {
        const text = state === 'inactive' ? 'Inactive since' : 'Member since';
        
        return (
            <div className="flex items-center py-3">
                <span>{text}</span>
                <span className="ml-auto">{`${time}`}</span>
            </div>
        )
    }

    function StateActions({ actions }: {actions?: QRreport.Action[]}) {

        const [reason, setReason] = useState('')
        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        let componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'ban-person': 
                     return (
                        <button onClick={() => setAuxAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            Ban
                        </button>
                    )
                case 'unban-person':
                    return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-yellow-700 hover:bg-yellow-900 text-white font-bold py-2 px-4 rounded">
                            Unban
                        </button>
                    )
                case 'delete-user':
                    return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            Delete
                        </button>
                    )
                case 'fire-person':
                    return (
                        <button onClick={() => setAuxAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                            Fire
                        </button>
                    )
                case 'rehire-person':
                    return (
                        <button onClick={() => setAction(action)} className="w-1/2 bg-yellow-400 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded">
                            Rehire
                        </button>
                    )
            }
        })

        return (
            <>
                <div className="flex space-x-2">
                    {componentsActions}
                </div>
                {auxAction !== undefined &&
                    <div>
                        <p>Insert bellow the reason:</p>
                        <textarea 
                            onChange={value => setReason(value.target.value)}
                            className={'block p-4 w-full text-gray-900 bg-gray-50 rounded-lg border border-gray-300'}/>
                        <button 
                            className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2"
                            onClick= {() => {setAction(auxAction); setAuxInfo(JSON.stringify({reason: reason}))}}>
                            Confirm
                        </button>
                    </div>
                }
            </>
        )
    }

    function UpdateAction({action}: {action: QRreport.Action | undefined}) {
        return action !== undefined ? (
            <button onClick={() => setAction(action)} className="my-1">
                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
            </button>
        ): <></>
    }   
    
    function AddSkillAction({ action }: { action: QRreport.Action }) {

        let href = action.properties.find(prop => prop.name == 'skill')
        const url = href === undefined ? '' : BASE_URL_API + href?.possibleValues?.href
        
        const { isFetching, isCanceled, cancel, result, error } = useFetch<Collection>(url, init)
        const [skillSelected, setSkill] = useState('')
        if (isFetching) return <Loading/>
        if (isCanceled) return <>Canceled</> //todo
        if (error) return <DisplayError/>
        return (
            <>
                <label htmlFor="underline_select" className="sr-only">Underline select</label>
                <select onChange={value => setSkill(value.target.value)} className="p-4 w-full text-gray-900 bg-gray-50 rounded-lg border border-gray-300">
                    <option selected>Choose a skill to add</option>
                    {getEntitiesOrUndefined(result?.body)?.map(skill => <option value={skill.properties.id}>{`${skill.properties.name}`}</option>)}
                </select>
                <button onClick={() => {setAction(action); setAuxInfo(JSON.stringify({skill: skillSelected}))}} 
                    className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                        Add skill
                </button>
            </>
        )
    }

    function RemoveSkillAction({ person, action }: { person: Person, action: QRreport.Action }) {

        const [skillSelected, setSkill] = useState('')

        return (
            <>
                <label htmlFor="underline_select" className="sr-only">Underline select</label>
                <select onChange={value => setSkill(value.target.value)} className="p-4 w-full text-gray-900 bg-gray-50 rounded-lg border border-gray-300">
                    <option selected>Choose a skill to delete</option>
                    {Array.from(person.skills!!).map(skill => <option value={skill}>{`${skill}`}</option>)}
                </select>
                <button onClick={() => {setAction(action); setAuxInfo(JSON.stringify({skill: skillSelected}))}} 
                    className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                        Remove skill
                </button>
            </>
        )
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
                        </div>
                        <ul className="bg-gray-100 text-gray-600 hover:text-gray-700 hover:shadow py-2 px-3 mt-3 divide-y rounded shadow-sm">
                            <UserState state={person.state}/>
                            <UserDate state={person.state} time={person.timestamp}/>
                        </ul>
                    </div>
                    <div>
                        <Link className="w-1/2" to={`/persons/${person.id}/tickets/`}>
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
            <div className="w-full bg-white p-3 shadow-sm rounded-sm">
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

    function Skills({ entity, actions }: {  entity: QRreport.Entity<Person> | undefined, actions?: QRreport.Action[]}) {
        const [currentAction, setCurrentAction] = useState<Action | undefined>(undefined)

        if (!entity) return null
        const person = entity.properties

        return (
            <div className="bg-white p-3 shadow-sm rounded-sm">
                <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                    <MdWork style= {{color: "green"}} /> <span className="tracking-wide">Skills</span>
                </div>
                <div className="text-gray-700 space-y-2">
                    {Array.from(person.skills!).map(skill => 
                        <span className='bg-blue-400 text-white rounded px-1'>{skill}</span>
                    )}
                    <div className="flex space-x-4">
                        {actions?.map(action => {
                            if (action.name === 'add-skill') {
                                return <button onClick={() => setCurrentAction(action)} className="px-2">
                                    <MdAddCircleOutline style= {{color: "green", fontSize: "1.5em"}}/>
                                </button>
                            }
                            if (action.name === 'remove-skill') {
                                return <button onClick={() => setCurrentAction(action)}>
                                    <MdRemoveCircleOutline style= {{color: "red", fontSize: "1.5em"}}/>
                                </button>
                            }}
                        )}
                    </div>

                    {currentAction?.name === 'add-skill' && <AddSkillAction action={currentAction}/>}
                    {currentAction?.name === 'remove-skill' && <RemoveSkillAction action={currentAction} person={person}/>}
                </div>
            </div>
        )
    }

    function Roles({ entity, actions }: {  entity: QRreport.Entity<Person> | undefined, actions?: QRreport.Action[]}) {
        const [currentAction, setCurrentAction] = useState<Action | undefined>(undefined)

        if (!entity) return null
        const person = entity.properties

        return (
            <div className="bg-white p-3 shadow-sm rounded-sm">
                <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                    <MdOutlineAssignmentInd style= {{color: "green"}} /> <span className="tracking-wide">Roles</span>
                </div>
                <div className="text-gray-700 space-y-2">
                    {Array.from(person.roles!).map(role => 
                        <span className='bg-blue-400 text-white rounded px-1'>{`${role}`}</span>
                    )}
                    <div className="flex space-x-4">
                        {actions?.map(action => {
                            if (action.name === 'add-role') {
                                return <button onClick={() => setCurrentAction(action)} className="px-2">
                                    <MdAddCircleOutline style= {{color: "green", fontSize: "1.5em"}}/>
                                </button>
                            }
                            if (action.name === 'remove-role') {
                                return <button onClick={() => setCurrentAction(action)}>
                                    <MdRemoveCircleOutline style= {{color: "red", fontSize: "1.5em"}}/>
                                </button>
                            }}
                        )}
                    </div>

                    {currentAction?.name === 'add-role' && <AddSkillAction action={currentAction}/>}
                    {currentAction?.name === 'remove-role' && <RemoveSkillAction action={currentAction} person={person}/>}
                </div>
            </div>
        )
    }

    const entity = getEntityOrUndefined(result?.body)
    console.log(entity?.actions)
    return (
    <>
        {result?.body?.type === 'problem' || entity === undefined ? <DisplayError/> :
        <div className="mx-auto my-auto p-5">
            <div className="md:flex no-wrap md:-mx-2">
                <MainInfo entity = {entity} actions={getActionsOrUndefined(result?.body)}/>
                <div className="w-full space-y-2">
                    <About entity = {entity}/>
                    {entity.properties.roles.find(role => role === 'employee') &&
                    <Skills entity = {entity} actions={getActionsOrUndefined(result?.body)}/>}
                    <Roles entity = {entity} actions={getActionsOrUndefined(result?.body)}/>
                </div>
            </div>
        </div>}
    </>
    )
}