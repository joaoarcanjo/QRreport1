import { useState } from "react"
import { Category, Person } from "../../models/Models"
import { Action } from "../../models/QRJsonModel"
import * as QRreport from '../../models/QRJsonModel';
import { MdAddCircleOutline, MdWork, MdRemoveCircleOutline } from "react-icons/md";
import { SelectCategory } from "../../category/SelectCategory";

export function Skills({ entity, actions, setAction, setPayload }: {  
    entity: QRreport.Entity<Person>, 
    actions?: QRreport.Action[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
}) {
    const [currentAction, setCurrentAction] = useState<Action | undefined>(undefined)

    const [ skill, setSkill ] = useState<Category | undefined>(undefined)

    if (!entity) return null
    const person = entity.properties

    const onSubmitHandler = () => {
        if(!skill) return
        setPayload(JSON.stringify({skill: skill.id}))
        setAction(currentAction)
    }

    return (
        <div className="bg-white p-3 shadow-sm rounded-sm space-y-2">
            <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                <MdWork style= {{color: "green"}} /> <span className="tracking-wide">Skills</span>
            </div>
            <div className="text-gray-700 space-y-2 space-x-2">
                {Array.from(person.skills!).map((skill, idx) => 
                    <span key={idx} className='bg-blue-400 text-white rounded px-1'>{skill}</span>
                )}
                <div className="flex space-x-4">
                    {actions?.map((action, idx) => {
                        if (action.name === 'add-skill') {
                            return <button key={idx} onClick={() => setCurrentAction(action)} className="px-2">
                                <MdAddCircleOutline style= {{color: "green", fontSize: "1.5em"}}/>
                            </button>
                        }
                        if (action.name === 'remove-skill') {
                            return <button key={idx} onClick={() => setCurrentAction(action)}>
                                <MdRemoveCircleOutline style= {{color: "red", fontSize: "1.5em"}}/>
                            </button>
                        }}
                    )}
                </div>
                {currentAction &&
                 <p>Skill selected: {skill === undefined ? '-----' : `${skill.name}`}</p>}
                {currentAction?.name === 'add-skill' && 
                <SelectCategory action={currentAction} setPayload={setSkill} setAuxAction={setCurrentAction} propName={"skill"}/>}
                {currentAction?.name === 'remove-skill' && 
                <SelectCategory action={currentAction} setPayload={setSkill} setAuxAction={setCurrentAction} propName={"skill"}/>}
            </div>
            <div>
                {currentAction &&
                    <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2" onClick={onSubmitHandler}>
                    {currentAction?.title}
                    </button>}
            </div>
        </div>
    )
}