import { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, BigSubmitButton, TextArea } from "../../components/form/FormComponents";
import { simpleInputForm, simpleTextAreaForm, phoneInputForm, emailInputForm } from "../../components/form/FormInputs";
import { ListPossibleValues } from "../../components/form/ListPossibleValues"
import { FormInfo } from "../../models/Models";
import { Action, Entity } from "../../models/QRJsonModel";
import { EMAIL_KEY, NAME_KEY, useLoggedInState } from '../../user/Session'
import ReCAPTCHA from "react-google-recaptcha";
import { BsBuilding, BsDoorClosed } from "react-icons/bs";
import { FaRegBuilding, FaToilet } from "react-icons/fa";

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
        payload['description'] = description === undefined || description === '' ? anomaly : description
        payload['hash'] = hash
        payload['name'] = name
        payload['email'] = email
        
        setAction(action)
        setPayload(JSON.stringify(payload))
    })

    function Headers() {
        const headerInfo = entity.properties

        return(
            <Header heading='Welcome to QRreport'>
                <HeaderParagraph paragraph='Tell us more about the problem found'/>
                <div className='mt-6 space-y-4'>
                    <div className='flex'>
                        <BsBuilding style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {headerInfo.company}</span>
                    </div>
                    <div className='flex'>
                        <FaRegBuilding style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {headerInfo.building}</span>
                    </div>
                    <div className='flex '>
                        <BsDoorClosed style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {headerInfo.room} </span>
                    </div> 
                    <div className='flex'>
                        <FaToilet style= {{ color: 'green', fontSize: "1.4em" }} /> 
                        <span>: {headerInfo.device} </span>
                    </div>
                </div>
            </Header>
        )
    }

   

    const[anomaly, setAnomaly] = useState('-1')

    const componentsInputs = useMemo(() => {
        return action!!.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'subject': return (anomaly === '-1') && <Input key={idx} value={simpleInputForm(register, 'Subject', errors, anomaly === '-1' ? undefined: false, prop.name, prop.type)}/>
                case 'description': return <TextArea key={idx}  value={simpleTextAreaForm('Description', register, errors, prop.required, prop.name, '')}/>
                case 'name': return !loggedState?.isLoggedIn && <Input key={idx} value={simpleInputForm(register, 'Name', errors, !loggedState?.isLoggedIn && prop.required, prop.name, prop.type)}/>
                case 'phone': return !loggedState?.isLoggedIn && <Input key={idx} value={phoneInputForm(register, errors, !loggedState?.isLoggedIn && prop.required, 'phone')}/> 
                case 'email': return !loggedState?.isLoggedIn && <Input key={idx} value={emailInputForm(register, errors, !loggedState?.isLoggedIn && prop.required, 'email')}/>
                case 'anomaly': return <ListPossibleValues key={idx} 
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
                {componentsInputs}
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