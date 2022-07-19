import { useForm } from "react-hook-form"
import { AiFillCloseCircle } from "react-icons/ai"
import { Form, LittleSubmitButton, TextArea } from "../../components/form/FormComponents"
import { simpleTextAreaForm } from "../../components/form/FormInputs"
import { ListPossibleValues } from "../../components/form/ListPossibleValues"
import { CloseButton } from "../../components/Various"
import { Action } from "../../models/QRJsonModel"

export function FireAction({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
}) {
    type roleData = { 
        company: number,
        reason: string 
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ company, reason }) => {
        action.href = action.href.replace('{companyId}', company.toString())
        setAction(action)
        setPayload(JSON.stringify({reason: reason}))
    })
    
    function Inputs() {
        let componentsInputs = action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'company': return <ListPossibleValues key={idx} 
                    register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select company'}/>
                case 'reason': return <TextArea key={idx} value={simpleTextAreaForm('Reason', register, errors, prop.required,  prop.name, 'Insert the reason')}/>
            }
        })
        return <>{componentsInputs}</>
    }

    const cancelForm = (event: any) => {
        event.preventDefault()
        setAuxAction(undefined)
    };

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200 shadow-md">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div>)
}