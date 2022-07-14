import { useForm } from "react-hook-form"
import { Form } from "../components/form/FormComponents"
import { ListPossibleValues } from "../components/form/ListPossibleValues"
import { Action } from "../models/QRJsonModel"

export function AddRoomDevice({action, setAction, setAuxAction, setPayload}: {
    action: Action,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type roomData = { 
        device: string 
    }

    const { register, handleSubmit } = useForm<roomData>()

    if(!action || !setAction || !setPayload || !setPayload) return null

    const onSubmitHandler = handleSubmit(({ device }) => {
        setAction(action)
        setPayload(JSON.stringify({deviceId: device}))
    })

    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'device': return <ListPossibleValues register={register} regName={prop.name} href={prop.possibleValues?.href}  listText={'Select new device'}/> 
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