import { useForm } from "react-hook-form";
import { Form, Input, LittleSubmitButton } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";

export function CreateRoom({action, setAction, setAuxAction, setPayload }: {  
    action?: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type buildingData = {
        name: string,
        floor: number
    }

    const { register, handleSubmit, formState: { errors } } = useForm<buildingData>();

    if(!action || !setAction || !setAuxAction || !setPayload) return null

    const onSubmitHandler = handleSubmit(({ name, floor }) => {
        setAction(action)
        setPayload(JSON.stringify({name: name, floor: floor}))
    })

    function Inputs() {
        let componentsInputs = action!!.properties.map((prop, idx) => {
            
            switch (prop.name) {
                case 'name': return <Input key={idx} value={simpleInputForm(register, 'Name', errors, prop.required, prop.name, prop.type)}/>
                case 'floor': return <Input key={idx} value={simpleInputForm(register, 'Floor', errors, prop.required, prop.name, prop.type)}/>
            }
        })
        return <>{componentsInputs}</>
    }
    
    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div> 
    )
}