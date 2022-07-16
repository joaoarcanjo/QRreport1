import { useForm } from "react-hook-form"
import { Form } from "../components/form/FormComponents"
import { ListPossibleValues } from "../components/form/ListPossibleValues"
import { Action } from "../models/QRJsonModel"

export function ChangeManager({action, setAction, setPayload}: {
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type managerData = { 
        managerId: string 
    }

    const { register, handleSubmit } = useForm<managerData>()

    if(!action || !setAction || !setPayload || !setPayload) return null

    const onSubmitHandler = handleSubmit(({ managerId }) => {
        setAction(action)
        console.log(managerId)
        setPayload(JSON.stringify({manager: managerId}))
    })

    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            console.log(prop.possibleValues?.href)
            switch (prop.name) {
                case 'managerId': return <ListPossibleValues register={register} regName={prop.name} href={prop.possibleValues?.href}  listText={'Select new manager'}/> 
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