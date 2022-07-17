import { useForm } from "react-hook-form"
import { AiFillCloseCircle } from "react-icons/ai"
import { TextArea, Form } from "../../components/form/FormComponents"
import { simpleTextAreaForm } from "../../components/form/FormInputs"
import { ListPossibleValues } from "../../components/form/ListPossibleValues"
import { Action } from "../../models/QRJsonModel"

export function AssignToCompany({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
}) {
    type roleData = { 
        company: string 
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ company }) => {
        setAction(action)
        setPayload(JSON.stringify({company: company}))
    })
    
    function Inputs() {
        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'company': return <ListPossibleValues 
                register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select company'}/>
            }
        })
        return <>{componentsInputs}</>
    }

    return (
        <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md">
            <button onClick={() => setAuxAction(undefined)}>
                <AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
            </button>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <button
                    className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                        {action.title}
                </button>
            </Form>
        </div>
    )
}