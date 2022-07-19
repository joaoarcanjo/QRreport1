import { useForm } from "react-hook-form";
import { Form, Input, LittleSubmitButton } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";

export function InputAnomaly({action, setAction, setAuxAction, setPayload }: {  
    action?: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type InputAmonaly = {
        anomaly: string
    }

    const { register, handleSubmit, formState: { errors } } = useForm<InputAmonaly>();

    if(!action) return null

    const onSubmitHandler = handleSubmit(({ anomaly }) => {
        setAction(action)
        setPayload(JSON.stringify({anomaly: anomaly}))
    })

    function Inputs() {
        let componentsInputs = action!!.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'anomaly': return <Input key={idx} value={simpleInputForm(register, "Anomaly", errors, prop.required, prop.name, prop.type)}/>
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