import { useForm } from "react-hook-form";
import { AiFillCloseCircle } from "react-icons/ai";
import { Form, Input, LittleSubmitButton } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { ListPossibleValues } from "../components/form/ListPossibleValues";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";

export function CreateBuilding({action, setAction, setAuxAction, setPayload }: {  
    action?: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type buildingData = {
        name: string,
        floors: number,
        managerId: string
    }

    const { register, handleSubmit, formState: { errors } } = useForm<buildingData>();

    if(!action || !setAction || !setAuxAction || !setPayload) return null

    const onSubmitHandler = handleSubmit(({ name, floors, managerId }) => {
        setAction(action)
        setPayload(JSON.stringify({name: name, floors: floors, manager: managerId}))//todo
    })

    function Inputs() {
        let componentsInputs = action!!.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'name': return <Input key={idx} value={simpleInputForm(register, "Name", errors, prop.required, prop.name, prop.type)}/>
                case 'floors': return <Input key={idx} value={simpleInputForm(register, "Floors", errors, prop.required, prop.name, prop.type)}/>
                case 'managerId': return <ListPossibleValues key={idx}  
                    register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select manager'}/>
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