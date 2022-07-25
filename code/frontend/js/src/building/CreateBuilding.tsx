import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { Input } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { CloseButton } from "../components/Various";
import { Person } from "../models/Models";
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

    const [ manager, setManager ] = useState<Person | undefined>(undefined)

    const { register, handleSubmit, formState: { errors } } = useForm<buildingData>();

    const onSubmitHandler = handleSubmit(({ name, floors }) => {
        if(manager === undefined) return
        const managerId = manager.id

        setAction(action)
        setPayload(JSON.stringify({name: name, floors: floors, manager: managerId}))
    })

    const selectManager = useMemo(() => {
        return <SelectManager action={action} setPayload={setManager} setAction={undefined} setAuxAction={undefined} />
    },[])

    let componentsInputs = action!!.properties.map((prop, idx) => {
        switch (prop.name) {
            case 'name': return <Input key={idx} value={simpleInputForm(register, "Name", errors, prop.required, prop.name, prop.type)}/>
            case 'floors': return <Input key={idx} value={simpleInputForm(register, "Floors", errors, prop.required, prop.name, prop.type)}/>
        }
    })

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            {componentsInputs}
            <p>Manager selected: {manager === undefined ? '-----' : `${manager.name}`}</p>
            {selectManager}
            <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2" onClick={onSubmitHandler}>
                {action.title}
            </button>
        </div> 
    )
}