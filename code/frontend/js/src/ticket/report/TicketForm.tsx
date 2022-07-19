import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, BigSubmitButton, TextArea } from "../../components/form/FormComponents";
import { simpleInputForm, simpleTextAreaForm, phoneInputForm, emailInputForm } from "../../components/form/FormInputs";
import { ListPossibleValues } from "../../components/form/ListPossibleValues"
import { FormInfo } from "../../models/Models";
import { Action, Entity } from "../../models/QRJsonModel";
import { EMAIL_KEY, NAME_KEY, useLoggedInState } from '../../user/Session'
import ReCAPTCHA from "react-google-recaptcha";

export function TicketForm({hash, entity, action, setAction, setPayload}: {
    hash: string, entity: Entity<FormInfo>, action: Action,
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
    const { register, handleSubmit, formState: { errors }} = useForm<ticketData>()
    const [captcha, setVerified] = useState(false)

    const onSubmitHandler = handleSubmit(({ subject , description, anomaly, name, phone, email }) => {
        if(!captcha) return 
        const payload: any = {}
        
        if (phone !== undefined && phone.length !== 0) {
            payload['phone'] = phone
        }

        const sessionName = sessionStorage.getItem(NAME_KEY)
        const sessionEmail = sessionStorage.getItem(EMAIL_KEY)
        
        if (loggedState?.isLoggedIn && sessionName && sessionEmail) {
            name = sessionName
            email = sessionEmail
        }
        
        payload['subject'] = anomaly === '-1' ? subject : anomaly
        payload['description'] = description === undefined ? anomaly : description
        payload['hash'] = hash
        payload['name'] = name
        payload['email'] = email

        setAction(action)
        setPayload(JSON.stringify(payload))
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

    const[anomaly, setAnomaly] = useState('-1')

    const ComponentsInputs = useMemo(() => {
        return action!!.properties.map(prop => {
            switch (prop.name) {
                case 'subject': return (anomaly === '-1') && <Input value={simpleInputForm(register, 'Subject', errors, anomaly === '-1' ? undefined: false, prop.name, prop.type)}/>
                case 'description': return <TextArea value={simpleTextAreaForm('Description', register, errors, prop.required, prop.name, '')}/>
                case 'name': return !loggedState?.isLoggedIn && <Input value={simpleInputForm(register, 'Name', errors, !loggedState?.isLoggedIn && prop.required, prop.name, prop.type)}/>
                case 'phone': return !loggedState?.isLoggedIn && <Input value={phoneInputForm(register, errors, !loggedState?.isLoggedIn && prop.required, 'Phone number')}/> 
                case 'email': return !loggedState?.isLoggedIn && <Input value={emailInputForm(register, errors, !loggedState?.isLoggedIn && prop.required, 'Email')}/> 
                case 'anomaly': return <ListPossibleValues 
                   register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select anomaly'} 
                   otherValueText={'Other problem...'}
                   setValue={setAnomaly}/>
            }
        })
    }, [action, anomaly, loggedState?.isLoggedIn, errors, register])

    return (
        <section className='info-section'>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Headers/>
                {ComponentsInputs}
                <ReCAPTCHA
                    sitekey="6LeeU_kgAAAAAFd1CrwpQK-qul76uXMT3SySXYYZ"
                    onChange={()=>{setVerified(true)}}
                />
                <BigSubmitButton text={'Submit'}/>
                <p> (*) Required</p>
            </Form>
        </section>
    )
}