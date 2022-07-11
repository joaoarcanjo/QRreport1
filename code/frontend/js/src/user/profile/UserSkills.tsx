import { useState } from "react"
import { Person } from "../../Models"
import { Action } from "../../models/QRJsonModel"
import * as QRreport from '../../models/QRJsonModel';
import { MdAddCircleOutline, MdWork, MdRemoveCircleOutline } from "react-icons/md";
import { Form } from "../../components/form/FormComponents";
import { useForm } from "react-hook-form";
import { ListPossibleValues } from "../../components/form/ListPossibleValues";

export function Skills({ entity, actions, setAction, setAuxInfo }: {  
    entity: QRreport.Entity<Person> | undefined, 
    actions?: QRreport.Action[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setAuxInfo: React.Dispatch<React.SetStateAction<string>> | undefined,
}) {
    const [currentAction, setCurrentAction] = useState<Action | undefined>(undefined)

    if (!entity || !setAction || !setAuxInfo) return null
    const person = entity.properties

    return (
        <div className="bg-white p-3 shadow-sm rounded-sm">
            <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                <MdWork style= {{color: "green"}} /> <span className="tracking-wide">Skills</span>
            </div>
            <div className="text-gray-700 space-y-2 space-x-2">
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

                {currentAction?.name === 'add-skill' && 
                <SkillAction action={currentAction} setAction={setAction} setAuxInfo={setAuxInfo}/>}
                {currentAction?.name === 'remove-skill' && 
                <SkillAction action={currentAction} setAction={setAction} setAuxInfo={setAuxInfo}/>}
            </div>
        </div>
    )
}

function SkillAction({ action, setAction, setAuxInfo }: { 
    action: QRreport.Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxInfo: React.Dispatch<React.SetStateAction<string>>,
}) {
    type roleData = { skill: string }

    const { register, handleSubmit } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ skill }) => {
        setAuxInfo(JSON.stringify({skill: skill}))
        setAction(action)
    })

    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'skill': return <ListPossibleValues 
                    register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select skill'} otherValueText={'None'}/>
            }
        })
        return <>{componentsInputs}</>
    }

    return <div>
        <Form onSubmitHandler = { onSubmitHandler }>
            <Inputs/>
            <button
                className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                    {action.title}
            </button>
        </Form>
    </div>
}