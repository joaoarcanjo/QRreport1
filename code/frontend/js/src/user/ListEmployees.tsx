import { useState } from "react"
import { Link } from "react-router-dom"
import { AiFillTool, AiFillStar } from "react-icons/ai";
import { Person } from "../Types";

export function ListEmployees() {

    function PersonItemComponent({person}: {person: Person}) {

        return (
            <div className="p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100">  
                <div className="w-full flex">
                    <div className="w-1/2">
                        <Link to={`/persons/${person.id}`}>
                            <h5 className="mb-2 text-xl font-bold tracking-tight text-gray-900">{person.name}</h5>
                        </Link>
                    </div>
                    <div className="w-1/2 flex justify-end space-x-4">
                        {/*temos que obter o numero de trabalhos e a votação*/}
                        <span className='flex items-center'>1<AiFillTool style= {{ fontSize: '1.5em' }}/></span>
                        <span className='flex items-center'>2<AiFillStar style= {{ color: 'yellow', fontSize: '1.5em' }}/></span>
                    </div>
                </div>
                <p>{person.email}</p>
            </div>
        )
    }

    const mockPersons = [
        {"id": "1",
        "name": "Carlos",
        "phone": "965520229",
        "email": "carlitos@gmail.com",
        "state": { 'id': 1, 'name': 'active' },
        "roles": [{"name": "user"}],
        "skills": []},
        {"id": "2",
        "name": "André",
        "phone": "965520229",
        "email": "andré@gmail.com",
        "state": { 'id': 1, 'name': 'active' },
        "roles": [{"name": "user"}],
        "skills": []},
        {"id": "3",
        "name": "Alfredo",
        "phone": "965520229",
        "email": "alfredo@gmail.com",
        "state": { 'id': 1, 'name': 'active' },
        "roles": [{"name": "user"}],
        "skills": []}
    ]

    return (
        <div className="px-3 pt-3 space-y-3">
            <h1 className="text-3xl mt-0 mb-2 text-blue-800">Employees</h1>
            {Array.from(mockPersons).map((person, idx) => <PersonItemComponent key={idx} person={person}/>)}
            <button className="py-2 px-4 bg-green-600 hover:bg-green-700 rounded-md text-white text-sm">
                Add employee
            </button>
        </div>
    )
}