import { useState } from "react"
import { CloseButton } from "../../components/Various"
import { Person } from "../../models/Models"
import { Action, Entity } from "../../models/QRJsonModel"
import { useLoggedInState } from "../Session"

export function SwitchRole({person, action, setPayload, setAction, setAuxAction}: {
    person: Person,
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
}) {
    const [currentRole, setRole] = useState<String>()
    const userSession = useLoggedInState()
    
    const roles = person.roles
    const activeRole = userSession?.userRole

    return (
        <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <p>Select role:</p>
            <div className="space-x-2">
                {Array.from(roles).map((role, idx) => {
                    return role !== activeRole ? (
                    <button key={idx} className='bg-blue-400 hover:bg-blue-800 text-white rounded px-1'
                            onClick= {() => setRole(role)}>
                        {role}
                    </button>): null
                })}
            </div>
            <div>
                {currentRole !== undefined ? 
                <button className='w-full bg-green-500 hover:bg-green-600 text-white rounded px-1 py-1'
                        onClick={() => {setAction(action); setPayload(JSON.stringify({role: currentRole}))}}>
                    {`Change role to ${currentRole}`}
                </button> : null}
            </div>
        </div>
    )
}