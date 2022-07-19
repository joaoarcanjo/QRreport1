import { useForm } from "react-hook-form"
import { AiFillCloseCircle } from "react-icons/ai"
import { Form, LittleSubmitButton } from "../../components/form/FormComponents"
import { ListPossibleValues } from "../../components/form/ListPossibleValues"
import { CloseButton } from "../../components/Various"
import { Action } from "../../models/QRJsonModel"

export function SwitchRole({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
}) {

    type roleData = { 
        role: string 
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ role }) => {
        setAction(action)
        setPayload(JSON.stringify({role: role}))
    })
    
    function Inputs() {
        let componentsInputs = action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'role': return <ListPossibleValues key={idx} register={register} regName={prop.name} href={prop.possibleValues?.href}  listText={'Select role'}/>
            }
        })
        return <>{componentsInputs}</>
    }

    return (
        <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div>)
}