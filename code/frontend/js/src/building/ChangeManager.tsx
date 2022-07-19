import { useForm } from "react-hook-form"
import { AiFillCloseCircle } from "react-icons/ai"
import { Form, LittleSubmitButton } from "../components/form/FormComponents"
import { ListPossibleValues } from "../components/form/ListPossibleValues"
import { CloseButton } from "../components/Various"
import { Action } from "../models/QRJsonModel"

export function ChangeManager({action, setAction, setPayload, setAuxAction}: {
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
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
        let componentsInputs = action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'managerId': return <ListPossibleValues key={idx} register={register} regName={prop.name} href={prop.possibleValues?.href}  listText={'Select new manager'}/> 
            }
        })
        return <>{componentsInputs}</>
    }

    return  <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
        <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
        <Form onSubmitHandler = { onSubmitHandler }>
            <Inputs/>
            <LittleSubmitButton text={`${action.title}`}/>
        </Form>
    </div>
}