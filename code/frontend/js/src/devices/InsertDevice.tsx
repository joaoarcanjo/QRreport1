import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { SelectCategory } from "../category/SelectCategory";
import { Form, Input } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { CloseButton } from "../components/Various";
import { Category } from "../models/Models";
import { Action } from "../models/QRJsonModel";

export function InsertDevice({action, setAction, setAuxAction, setPayload }: {  
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type deviceData = {
        name: string
    }

    const { register, handleSubmit, formState: { errors } } = useForm<deviceData>();
    const [ category, setCategory ] = useState<Category | undefined>(undefined)

    const onSubmitHandler = handleSubmit(({ name }) => {
        if(!category) return
        setAction(action)
        setPayload(JSON.stringify({name: name, category: category.id}))
    })

    const categoryInput = useMemo(() => {
        return <SelectCategory action={action} setPayload={setCategory} setAuxAction={undefined} propName={"category"}/>
    }, [action])

    function Inputs() {
        let componentsInputs = action!!.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'name': return <Input key={idx}
                    value={simpleInputForm(register, 'Name', errors, prop.required, prop.name, prop.type)}/>
                }
        })
        return <>{componentsInputs}</>
    }
    
    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
            </Form>
            <p>Category selected: {category === undefined ? '-----' : `${category.name}`}</p>
            {categoryInput}
            <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2" onClick={onSubmitHandler}>
                {action.title}
            </button>
        </div> 
    )
}