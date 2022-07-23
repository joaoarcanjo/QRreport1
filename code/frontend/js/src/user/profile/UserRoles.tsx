import { useMemo, useState } from "react"
import { Person } from "../../models/Models"
import { Action } from "../../models/QRJsonModel"
import * as QRreport from '../../models/QRJsonModel';
import { MdAddCircleOutline, MdRemoveCircleOutline, MdOutlineAssignmentInd } from "react-icons/md";
import { useForm } from "react-hook-form";
import { Form, Input, LittleSubmitButton } from "../../components/form/FormComponents";
import { simpleInputForm } from "../../components/form/FormInputs";
import { ListPossibleValues, LIST_DEFAULT_VALUE } from "../../components/form/ListPossibleValues";
import { CloseButton } from "../../components/Various";
import { EMPLOYEE_ROLE } from "../Session";

export function Roles({ entity, actions, setAction, setPayload }: {  
    entity: QRreport.Entity<Person>, 
    actions?: QRreport.Action[],
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
}) {
    const [currentAction, setCurrentAction] = useState<Action | undefined>(undefined)
    const person = entity.properties

    return (
        <div className="bg-white p-3 shadow-sm rounded-sm">
            <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                <MdOutlineAssignmentInd style= {{color: "green"}} /> <span className="tracking-wide">Roles</span>
            </div>
            <div className="text-gray-700 space-y-2 space-x-2">
                {Array.from(person.roles!).map((role, idx) => 
                    <span key={idx} className='bg-blue-400 text-white rounded px-1'>{`${role}`}</span>
                )}
                <div className="flex space-x-4">
                    {actions?.map((action, idx) => {
                        if (action.name === 'add-role') {
                            return <button key={idx} onClick={() => setCurrentAction(action)} className="px-2">
                                <MdAddCircleOutline style= {{color: "green", fontSize: "1.5em"}}/>
                            </button>
                        }
                        if (action.name === 'remove-role') {
                            return <button key={idx} onClick={() => setCurrentAction(action)}>
                                <MdRemoveCircleOutline style= {{color: "red", fontSize: "1.5em"}}/>
                            </button>
                        }}
                    )}
                </div>

                {currentAction?.name === 'add-role' && 
                <AddRoleAction action={currentAction} setAction={setAction} setAuxInfo={setPayload} setCurrentAction={setCurrentAction}/>}
                {currentAction?.name === 'remove-role' && 
                <RemoveRoleAction action={currentAction} setAction={setAction} setAuxInfo={setPayload} setCurrentAction={setCurrentAction}/>}

            </div>
        </div>
    )
}

function AddRoleAction({ action, setAction, setAuxInfo, setCurrentAction }: { 
    action: QRreport.Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxInfo: React.Dispatch<React.SetStateAction<string>>,
    setCurrentAction: React.Dispatch<React.SetStateAction<Action | undefined>>
}) {

    type roleData = {
        role: string, 
        company: string,
        skill: string,
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ role, company, skill }) => {

        if(company === LIST_DEFAULT_VALUE || (role === EMPLOYEE_ROLE && skill === LIST_DEFAULT_VALUE)) return

        const payload: any = {}

        payload['role'] = role === '' ? null : role
        payload['company'] = company === LIST_DEFAULT_VALUE ? null : company
        payload['skill'] = skill === LIST_DEFAULT_VALUE ? null : skill
        
        setAction(action)
        setAuxInfo(JSON.stringify(payload))
    })

    const componentsInputs = useMemo(() => {
        return action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'role': return <Input key={idx} value={simpleInputForm(register, "Role", errors, prop.required, prop.name, prop.type)}/>
                case 'company': return <ListPossibleValues key={idx} register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select company'}/>
                case 'skill': return <ListPossibleValues key={idx} register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select skill'} otherValueText={'None'}/>
            }
        })
    }, [action])
    
    const cancelForm = (event: any) => {
        event.preventDefault()
        setCurrentAction(undefined)
    };

    return <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md">
        <CloseButton onClickHandler={ cancelForm }/>
        <Form onSubmitHandler = { onSubmitHandler }>
            {componentsInputs}
            <LittleSubmitButton text={`${action.title}`}/>
        </Form>
    </div>
}

function RemoveRoleAction({ action, setAction, setAuxInfo, setCurrentAction }: { 
    action: QRreport.Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxInfo: React.Dispatch<React.SetStateAction<string>>,
    setCurrentAction: React.Dispatch<React.SetStateAction<Action | undefined>>
}) {

    type roleData = { role: string }
    
    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ role }) => {
        setAuxInfo(JSON.stringify({role: role}))
        setAction(action)
    })

    function Inputs() {
        let componentsInputs = action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'role': return <Input key={idx} value={simpleInputForm(register, "Role", errors, prop.required, prop.name, prop.type)}/>
            }
        })
        return <>{componentsInputs}</>
    }

    const cancelForm = (event: any) => {
        event.preventDefault()
        setCurrentAction(undefined)
    };

    return <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md">
        <CloseButton onClickHandler={ cancelForm }/>
        <Form onSubmitHandler = { onSubmitHandler }>
            <Inputs/>
            <LittleSubmitButton text={`${action.title}`}/>
        </Form>
    </div>
}