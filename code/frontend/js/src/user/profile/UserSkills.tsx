import { useState } from "react"
import { Person } from "../../models/Models"
import { Action } from "../../models/QRJsonModel"
import * as QRreport from '../../models/QRJsonModel';
import { MdAddCircleOutline, MdWork, MdRemoveCircleOutline } from "react-icons/md";
import { Form, LittleSubmitButton } from "../../components/form/FormComponents";
import { useForm } from "react-hook-form";
import { ListPossibleValues } from "../../components/form/ListPossibleValues";
import { CloseButton } from "../../components/Various";

export function Skills({ entity, actions, setAction, setPayload }: {  
    entity: QRreport.Entity<Person>, 
    actions?: QRreport.Action[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
}) {
    const [currentAction, setCurrentAction] = useState<Action | undefined>(undefined)

    if (!entity) return null
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
                <SkillAction action={currentAction} setAction={setAction} setPayload={setPayload} setCurrentAction={setCurrentAction}/>}
                {currentAction?.name === 'remove-skill' && 
                <SkillAction action={currentAction} setAction={setAction} setPayload={setPayload} setCurrentAction={setCurrentAction}/>}
            </div>
        </div>
    )
}

function SkillAction({ action, setAction, setPayload, setCurrentAction }: { 
    action: QRreport.Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setCurrentAction: React.Dispatch<React.SetStateAction<Action | undefined>>
}) {
    type roleData = { skill: string }

    const { register, handleSubmit } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ skill }) => {
        setPayload(JSON.stringify({skill: skill}))
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

    return <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md">
        <CloseButton onClickHandler={ () => setCurrentAction(undefined) }/>
        <Form onSubmitHandler = { onSubmitHandler }>
            <Inputs/>
            <LittleSubmitButton text={`${action.title}`}/>
        </Form>
    </div>
}