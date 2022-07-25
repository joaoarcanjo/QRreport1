import { Navigate, useParams } from 'react-router-dom';
import { FaEdit, FaUserAlt } from 'react-icons/fa';
import { Person } from '../../models/Models';
import { useEffect, useMemo, useState } from 'react';
import { useFetch } from '../../hooks/useFetch';
import { LOGIN_URL, PERSONS_URL, PERSON_PROFILE, PERSON_URL_API } from '../../Urls';
import { ErrorView } from '../../errors/Error';
import * as QRreport from '../../models/QRJsonModel';
import { getEntityOrUndefined, getActionsOrUndefined, getProblemOrUndefined } from '../../models/ModelUtils';
import { ActionComponent } from '../../components/ActionComponent';
import { Action } from '../../models/QRJsonModel';
import { Skills } from './UserSkills';
import { Roles } from './UserRoles';
import { FireAction } from './FireAction';
import { BanAction } from './BanAction';
import { EMPLOYEE_ROLE, useLoggedInState } from '../Session';
import { Loading, StateComponent } from '../../components/Various';
import { AssignToCompany } from './AssignToCompany';
import { SwitchRole } from './SwitchRole';
import { UpdateProfile } from './UpdateProfile';
import { RehireAction } from './RehireAction';

export function Profile() {
    
    let { personId } = useParams()
    const userSession = useLoggedInState()
    
    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<QRreport.Action | undefined>(undefined)
    const [payload, setPayload] = useState('')
    const [currentUrl, setCurrentUrl] = useState('')

    useEffect(() => {
        if(action?.name === 'switch-role') {
            const a = JSON.parse(payload)
            userSession?.changeRole(a.role)
        }
    }, [action])

    const { isFetching, result, error } = useFetch<Person>(currentUrl, init)

    if(userSession?.isLoggedIn && personId !== undefined && currentUrl !== PERSON_URL_API(personId)) 
        setCurrentUrl(PERSON_URL_API(personId))
    else if((userSession?.isLoggedIn && personId === undefined && currentUrl !== PERSON_PROFILE)) 
        setCurrentUrl( PERSON_PROFILE)
    else if(!userSession?.isLoggedIn) 
        return <Navigate to={LOGIN_URL}/>

    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    switch (action?.name) {
        case 'delete-user': return <ActionComponent redirectUrl={PERSONS_URL} action={action}/>
        case 'unban-person': return <ActionComponent action={action} returnComponent ={<Profile/>}/>
        case 'ban-person': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'rehire-person': return <ActionComponent action={action} returnComponent ={<Profile/>}/>
        case 'fire-person': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'add-skill': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'remove-skill': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'add-role': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'remove-role': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'switch-role': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'assign-to-company': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
        case 'update-person': return <ActionComponent action={action} extraInfo={payload} returnComponent ={<Profile/>}/>
    }

    function UserActions({ entity, actions }: {entity: QRreport.Entity<Person>, actions?: QRreport.Action[]}) {
        
        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)
        const person = entity.properties
        
        let userActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'ban-person': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)
                case 'unban-person': return (
                    <button key={idx} onClick={() => setAction(action)} className="w-1/2 bg-yellow-700 hover:bg-yellow-900 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)
                case 'delete-user': return (
                    <button key={idx} onClick={() => setAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)

                case 'switch-role': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-green-400 hover:bg-green-900 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)
            }
        })

        let employeeActions = actions?.map((action, idx) => {
            switch(action.name) {
                case 'fire-person': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-red-700 hover:bg-red-900 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)
                case 'rehire-person': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-yellow-400 hover:bg-yellow-600 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)
                case 'assign-to-company': return (
                    <button key={idx} onClick={() => setAuxAction(action)} className="w-1/2 bg-green-400 hover:bg-green-900 text-white py-2 px-2 rounded">
                        {action.title}
                    </button>)
            }
        })

        return (
            <>
                <div className="flex space-x-2"> {userActions} </div>
                <div className="flex space-x-2"> {employeeActions} </div>
                {auxAction?.name === 'fire-person' && 
                <FireAction action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
                {auxAction?.name === 'rehire-person' && 
                <RehireAction action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
                {auxAction?.name === 'ban-person' && 
                <BanAction action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
                {auxAction?.name === 'assign-to-company' && 
                <AssignToCompany action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
                {auxAction?.name === 'switch-role' && 
                <SwitchRole person={person} action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
            </>
        )
    }

    function UpdateAction({action, setUpdateAction}: {
        action: QRreport.Action | undefined,
        setUpdateAction: React.Dispatch<React.SetStateAction<QRreport.Action | undefined>>
    }) {
        return action !== undefined ? (
            <button onClick={() => setUpdateAction(action)} className="my-1">
                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
            </button>
        ): null
    }   

    function MainInfo({ entity, actions }: { entity: QRreport.Entity<Person>, actions?: QRreport.Action[] }){
        
        const [updateAction, setUpdateAction] = useState<Action>()
        const person = entity.properties

        return (
            <div className="md:w-5/12 md:mx-2 space-x-4">
                <div className="bg-white p-3 border-t-4 border-blue-900 space-y-3">
                    <div className="space-y-3">
                        <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                            <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{person.name.split(' ')[0]}</span>
                            <UpdateAction action={actions?.find(action => action.name === 'update-person')} setUpdateAction={setUpdateAction}/> 
                        </div>
                        <StateComponent state={person.state} timestamp={person.timestamp}/>
                        <div>
                            {updateAction && <UpdateProfile action={updateAction} setAction={setAction} setAuxAction={setUpdateAction} setPayload={setPayload}/>}
                        </div>
                    </div>  
                    <UserActions entity={entity} actions={actions}/>
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

    function About({ entity }: { entity: QRreport.Entity<Person> }) {
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
                    {person.phone && <DetailInfo name={'Contact No:'} value={person.phone}/>}
                    {isEmployee && <DetailInfo name={'Skills:'} value={person.skills?.map(skill => `${skill} ${' '}`)}/>}
                </div>
            </div>
        )
    }

    const entity = getEntityOrUndefined(result?.body)
    if(!entity) return null

    return userSession?.isLoggedIn ? (
    <>
        {result?.body?.type === 'problem' || entity === undefined ? <ErrorView /> :
        <div className="mx-auto my-auto p-5">
            <div className="md:flex no-wrap md:-mx-2">
                <MainInfo entity = {entity} actions={getActionsOrUndefined(result?.body)}/>
                <div className="w-full space-y-2">
                    <About entity = {entity}/>
                    {entity.properties.roles.find(role => role === EMPLOYEE_ROLE) &&
                    <Skills entity = {entity} actions={getActionsOrUndefined(result?.body)} setAction={setAction} setPayload={setPayload}/>}
                    <Roles entity = {entity} actions={getActionsOrUndefined(result?.body)} setAction={setAction} setPayload={setPayload}/>
                </div>
            </div>
        </div>}
    </>
    ) : <Navigate to={LOGIN_URL}></Navigate>
}