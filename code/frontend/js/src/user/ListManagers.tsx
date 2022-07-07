import { useState } from "react"
import { Link } from "react-router-dom"
import { PersonItem } from "../Types"

export function ListManagers() {

    function PersonItemComponent({person}: {person: PersonItem}) {

        return (
            <div className="p-5 bg-white rounded-lg border border-gray-200 shadow-md hover:bg-gray-100">  
                <Link to={`/persons/${person.id}`}>
                    <h5 className="mb-2 text-xl font-bold tracking-tight text-gray-900">{person.name}</h5>
                </Link>
                <p>{person.email}</p>
            </div>
        )
    }

    const mockPersons = [
        {'id': 1,
        'name': "Carlos",
        'phone': "965520229",
        'email': "carlitos@gmail.com",
        'state': { 'id': 1, 'name': 'active' },
        'roles': [{"name": "user"}],
        'skills': []},
        {'id': 2,
        'name': "André",
        'phone': "965520229",
        'email': "andré@gmail.com",
        'state': { 'id': 1, 'name': 'active' },
        'roles': [{"name": "user"}],
        'skills': []},
        {'id': 3,
        'name': "Alfredo",
        'phone': "965520229",
        'email': "alfredo@gmail.com",
        'state': { 'id': 1, 'name': 'active' },
        'roles': [{"name": "user"}],
        'skills': []}
    ]

    return (
        <div className="px-3 pt-3 space-y-4">
            <h1 className="text-3xl mt-0 mb-2 text-blue-800">Managers</h1>
            {Array.from(mockPersons).map((person, idx) => <PersonItemComponent key={idx} person={person}/>)}
        </div>
    )
}