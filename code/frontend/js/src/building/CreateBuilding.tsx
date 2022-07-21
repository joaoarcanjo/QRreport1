import { useState } from "react";
import { useForm } from "react-hook-form";
import { AiFillCloseCircle } from "react-icons/ai";
import { Form, Input, LittleSubmitButton } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { ListPossibleValues } from "../components/form/ListPossibleValues";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";
import { SelectManager } from "./SelectManager";

export function CreateBuilding({action, setAction, setAuxAction, setPayload }: {  
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type buildingData = {
        name: string,
        floors: number
    }

    const [ managerId, setManager ] = useState<string>('')

    const { register, handleSubmit, formState: { errors } } = useForm<buildingData>();

    const onSubmitHandler = handleSubmit(({ name, floors }) => {
        if(managerId === '') return
        const manager = JSON.parse(managerId).manager
        console.log(manager)

        setAction(action)
        setPayload(JSON.stringify({name: name, floors: floors, manager: manager}))//todo
    })

    let componentsInputs = action!!.properties.map((prop, idx) => {
        switch (prop.name) {
            case 'name': return <Input key={idx} value={simpleInputForm(register, "Name", errors, prop.required, prop.name, prop.type)}/>
            case 'floors': return <Input key={idx} value={simpleInputForm(register, "Floors", errors, prop.required, prop.name, prop.type)}/>
            case 'managerId': return <SelectManager key={idx} action={action} setPayload={setManager} setAction={undefined} setAuxAction={undefined} />
        }
    })

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                {componentsInputs}
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div> 
    )
}