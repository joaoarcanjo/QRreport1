import { useState } from "react";
import { State } from "../models/Models";
import { Action } from "../models/QRJsonModel";

export function UpdateState({states, action, setPayload, setAction}: {
    states: State[],
    action: Action | undefined,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {
    const [currentState, setState] = useState<State>()
    
    if(!action || !setAction || !setPayload) return null

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <p>Select new state:</p>
            <div className="space-x-2">
                {Array.from(states).map((state, idx) => 
                    <button key={idx} className='bg-blue-400 hover:bg-blue-800 text-white rounded px-1'
                            onClick= {() => setState(state)}>
                        {state.name}
                    </button>
                )}
            </div>
            <div>
                {currentState !== undefined ? 
                <button className='w-full bg-green-500 hover:bg-green-600 text-white rounded px-1 py-1'
                        onClick={() => {setAction(action); setPayload(JSON.stringify({state: currentState.id}))}}>
                    Change state to {currentState.name}
                </button> : null}
            </div>
        </div>
    )
}