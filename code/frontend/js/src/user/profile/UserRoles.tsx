import { useState } from "react"
import { Person } from "../../Models"
import { Action } from "../../models/QRJsonModel"
import * as QRreport from '../../models/QRJsonModel';
import { MdAddCircleOutline, MdRemoveCircleOutline, MdOutlineAssignmentInd } from "react-icons/md";
import { useForm } from "react-hook-form";
import { Form, Input } from "../../components/form/FormComponents";
import { simpleInputForm } from "../../components/form/FormInputs";
import { ListPossibleValues } from "../../components/form/ListPossibleValues";

export function Roles({ entity, actions, setAction, setAuxInfo }: {  
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
                <MdOutlineAssignmentInd style= {{color: "green"}} /> <span className="tracking-wide">Roles</span>
            </div>
            <div className="text-gray-700 space-y-2 space-x-2">
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

                {currentAction?.name === 'add-role' && <AddRoleAction action={currentAction} setAction={setAction} setAuxInfo={setAuxInfo}/>}
                {currentAction?.name === 'remove-role' && <RemoveRoleAction action={currentAction} setAction={setAction} setAuxInfo={setAuxInfo}/>}

            </div>
        </div>
    )
}

function AddRoleAction({ action, setAction, setAuxInfo }: { 
    action: QRreport.Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxInfo: React.Dispatch<React.SetStateAction<string>>,
}) {

    type roleData = {
        role: string, 
        company: number,
        skill: number,
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ role, company, skill }) => {
        const payload: any = {}

        payload['role'] = role === '' ? null : role
        payload['company'] = company == -1 ? null : company
        payload['skill'] = skill == -1 ? null : skill

        setAction(action)
        setAuxInfo(JSON.stringify(payload))
    })

    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'role': return <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/>
                case 'company': return <ListPossibleValues register={register} regName={prop.name} href={prop.possibleValues?.href}  listText={'Select company'} otherValueText={'None'}/>
                case 'skill': return <ListPossibleValues register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select skill'} otherValueText={'None'}/>
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

function RemoveRoleAction({ action, setAction, setAuxInfo }: { 
    action: QRreport.Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxInfo: React.Dispatch<React.SetStateAction<string>>,
}) {

    type roleData = { role: string }
    
    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ role }) => {
        setAuxInfo(JSON.stringify({role: role}))
        setAction(action)
    })

    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'role': return <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/>
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