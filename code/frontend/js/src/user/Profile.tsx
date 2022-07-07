import { Link, Navigate } from 'react-router-dom';
import { FaEdit, FaUserAlt } from "react-icons/fa";
import { useLoggedInState } from './Session';
import { Person, State } from '../Types';

/* Main user info section */
function UserState({state}: {state: State}) {

    const stateColor = state.name === 'inactive' ? 'bg-red-600' : 'bg-green-600';
    
    const stateElement = <span className={`${stateColor} ml-auto py-1 px-2 rounded text-white text-sm`}>{state.name}</span>
    
    return (
        <li className="flex items-center py-3">
            <span>Status</span>
            {stateElement}
        </li>
    )
}

type UserDateProps = {
    state: State;
    time: string;
}

function UserDate({state, time}: UserDateProps) {
    const text = state.name === 'inactive' ? 'Inactive since' : 'Member since';

    return (
        <div className="flex items-center py-3">
            <span>{text}</span>
            <span className="ml-auto">{time}</span>
        </div>
    )
}

function MainInfo({person} : {person: Person}) {

    const userSession = useLoggedInState()

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
                        <Link to = '/updateProfile'>
                            <button className="my-1">
                                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                            </button>
                        </Link>
                    </div>
                    <ul className="bg-gray-100 text-gray-600 hover:text-gray-700 hover:shadow py-2 px-3 mt-3 divide-y rounded shadow-sm">
                        <UserState state={person.state}/>
                        <UserDate state={person.state} time={person.timestamp}/>
                    </ul>
                </div>
                <div className="flex space-x-4">
                    <Link className="w-1/2" to={'/tickets'}>
                        <button className="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                            {person.roles.find(role => role.name === 'employee') ? 'Work': 'Tickets'}
                        </button>
                    </Link>
                    <button className="w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded">
                        Ban
                    </button>
                </div>
            </div>
        </div>
    )
}

/* About section */

type DetailInfoProp = {
    name: string;
    value: any;
}

function DetailInfo({name, value}: DetailInfoProp) {
    return (
        <div>
            <div className="px-4 py-2 font-semibold">{name}</div>
            <div className="px-4 py-2">{value}</div>
        </div>
    )
}

function About({person}: {person: Person}) {

    const isEmployee = person.roles.find(role => role.name === 'employee') !== null

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

    const personMock = {
        'id': 1,
        'name': 'Carlos Manuel Gonçalves',
        'phone': '965520229',
        'email': 'carlitos@gmail.com',
        'state': { 'id': 1, 'name': 'active' },
        'roles': [{'name': 'employee'}],
        'skills': [{'name': 'electricity'}, {'name': 'padeiro'},{'name': 'fornalhas'}],
        'timestamp': '2017-02-20'
    }

    return (
        <div className="container mx-auto my-5 p-5">
            <div className="md:flex no-wrap md:-mx-2">
                <MainInfo person = {personMock}/>
                <About person = {personMock}/>
            </div>
        </div>
    )
}