import { useState } from "react";
import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, SubmitButton, TextArea } from "../../components/form/FormComponents";
import { simpleInputForm, simpleTextAreaForm, phoneInputForm, emailInputForm } from "../../components/form/FormInputs";
import { ListPossibleValues } from "../../components/form/ListPossibleValues"
import { FormInfo } from "../../models/Models";
import { Action, Entity } from "../../models/QRJsonModel";
import { EMAIL_KEY, NAME_KEY, useLoggedInState } from '../../user/Session'

export function TicketInfo({hash, entity, action, setAction, setPayload}: {
    hash: string, entity: Entity<FormInfo>, action?: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type ticketData = { 
        subject: string, 
        description?: string, 
        anomaly: string,
        name: string, 
        phone: string
        email: string
    }
    
    const loggedState = useLoggedInState()

    const { register, handleSubmit, formState: { errors }} = useForm<ticketData>();

    if(!action) return null

    const onSubmitHandler = handleSubmit(({ subject , description, anomaly, name, phone, email }) => {
        const payload: any = {}

        console.log(subject, description, anomaly, name, phone, email)
        
        if (phone !== undefined && phone.length !== 0) {
            payload['phone'] = phone
        }

        //nao vamos colocar aqui, vamos caso o utilizador esteja logado, obter isto do token e publicar o ticket
        //o email, e o name também têm que ser opcionais para que possa chegar ao handler.
        //um utilizador nao autenticado nao está a conseguir submeter o ticket
        const sessionName = sessionStorage.getItem(NAME_KEY)
        const sessionEmail = sessionStorage.getItem(EMAIL_KEY)

        if (loggedState?.isLoggedIn && sessionName && sessionEmail) {
            name = sessionName
            email = sessionEmail
        }

        payload['subject'] = anomaly === '-1' ? subject : anomaly
        payload['description'] = description
        payload['hash'] = hash
        payload['name'] = name
        payload['email'] = email

        console.log(payload)
        //setAction(action)
        //setPayload(JSON.stringify(payload))
    })

    //todo: adicionar o icone do room e o icone do room
    function Headers() {
        const headerInfo = entity.properties

        return(
            <Header heading='Welcome to QRreport'>
                <HeaderParagraph paragraph='Tell us more about the problem found'/>
                <HeaderParagraph paragraph={`Building: ${headerInfo.building} | Room: ${headerInfo.room}`}/>
                <HeaderParagraph paragraph={`Company: ${headerInfo.company}`}/>
            </Header>
        )
    }
    
    function Inputs() {
    
        const[anomaly, setAnomaly] = useState('-1')
        if(!action) return null
    
        let componentsInputs = action!!.properties.map(prop => {
            switch (prop.name) {
                case 'subject': return (anomaly === '-1') && <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/>
                case 'description': return <TextArea value={simpleTextAreaForm(register, errors, prop.required, prop.name, 'Insert a description')}/>
                case 'name': return !loggedState?.isLoggedIn ? <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/> : <></>
                case 'phone': return !loggedState?.isLoggedIn ? <Input value={phoneInputForm(register, errors, prop.required, prop.name)}/> : <></>
                case 'email': return !loggedState?.isLoggedIn ? <Input value={emailInputForm(register, errors, prop.required, prop.name)}/> : <></>
                case 'anomaly': return <ListPossibleValues 
                   register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select anomaly'} 
                   otherValueText={'Other problem...'}
                   setValue={setAnomaly}/>
            }
        })
        return <>{componentsInputs}</>
    }

    return (
        <section className='info-section'>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Headers/>
                <Inputs/>
                <SubmitButton text={'Submit'}/>
                <p> (*) Required</p>
            </Form>
        </section>
    )
}