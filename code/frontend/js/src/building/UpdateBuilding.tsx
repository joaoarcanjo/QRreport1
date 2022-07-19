import { useForm } from "react-hook-form";
import { AiFillCloseCircle } from "react-icons/ai";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, BigSubmitButton, LittleSubmitButton } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";


export function UpdateBuilding({action, setAction, setAuxAction, setPayload }: {  
    action?: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type buildingData = {
        name: string,
        floors: number
    }

    const { register, handleSubmit, formState: { errors } } = useForm<buildingData>();

    if(!action || !setAction || !setAuxAction || !setPayload) return null

    const onSubmitHandler = handleSubmit(({ name, floors }) => {
        setAction(action)
        setPayload(JSON.stringify({name: name, floors: floors}))//todo
    })

    function Inputs() {
        let componentsInputs = action!!.properties.map(prop => {
            console.log(prop);
            switch (prop.name) {
                case 'name': return <Input value={simpleInputForm(register, 'Building name', errors, prop.required, prop.name, prop.type)}/>
                case 'floors': return <Input value={simpleInputForm(register, 'Number of floors', errors, prop.required, prop.name, prop.type)}/>
            }
        })
        return <>{componentsInputs}</>
    }
    
    return (
        <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div>         
    )
}