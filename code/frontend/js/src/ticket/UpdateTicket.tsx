import { useForm } from "react-hook-form";
import { AiFillCloseCircle } from "react-icons/ai";
import { Form, Header, HeaderParagraph, Input, Paragraph, BigSubmitButton } from "../components/form/FormComponents";
import { simpleInputForm } from "../components/form/FormInputs";
import { Action } from "../models/QRJsonModel";

export function UpdateTicket({ action, setAction, setAuxAction, setPayload}: { 
    action: Action | undefined,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type userData = {
        subject: string, 
        description: string,
    }

    const { register, handleSubmit, formState: { errors } } = useForm<userData>()
    
    const onSubmitHandler = handleSubmit(({ subject, description }) => {
        const payload: any = {}
        
        payload['subject'] = subject !== '' ? subject : null
        payload['description'] = description !== '' ? description : null

        setAction(action)
        setPayload(payload)
    })
    
    function Inputs() {
        if(!action) return null

        let componentsInputs = action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'subject': return <Input key={idx} value={simpleInputForm(register, 'Subject', errors, prop.required, prop.name, prop.type)}/>
                case 'description': return <Input key={idx} value={simpleInputForm(register, 'Description', errors, prop.required, prop.name, prop.type)}/> 
            }
        })
        return <>{componentsInputs}</>
    }
    
    return (
        <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200">
            <button onClick={() => setAuxAction(undefined)}>
                <AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
            </button>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Header heading='Update your ticket'>
                    <HeaderParagraph paragraph='Insert new information below'/>
                </Header>
                <Inputs/>
                <BigSubmitButton text={'Update ticket'}/>
                <Paragraph value = {'(*) Required'}/>
            </Form>
        </div>
    )
}