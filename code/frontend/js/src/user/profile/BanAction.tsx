import { useForm } from "react-hook-form"
import { AiFillCloseCircle } from "react-icons/ai"
import { Form, LittleSubmitButton, TextArea } from "../../components/form/FormComponents"
import { simpleTextAreaForm } from "../../components/form/FormInputs"
import { CloseButton } from "../../components/Various"
import { Action } from "../../models/QRJsonModel"

export function BanAction({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
}) {

    type roleData = { 
        reason: string 
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ reason }) => {
        setAction(action)
        setPayload(JSON.stringify({reason: reason}))
    })
    
    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'reason': return <TextArea value={simpleTextAreaForm('Reason', register, errors, prop.required,  prop.name, 'Insert the reason')}/>
            }
        })
        return <>{componentsInputs}</>
    }

    return (
        <div className="space-y-1 p-3 bg-white rounded-lg border border-gray-200 shadow-md">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div>)
}