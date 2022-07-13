import { useState } from "react";
import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, Paragraph, SubmitButton } from "../components/form/FormComponents";
import { emailInputForm, passwordInputForm, passwordVerifyInputForm, phoneInputForm, simpleInputForm } from "../components/form/FormInputs";
import { Loading } from "../components/Various";
import { DisplayError } from "../Error";
import { useFetch } from "../hooks/useFetch";
import * as QRreport from '../models/QRJsonModel';
import { BASE_URL_API } from "../Urls";
import { TicketRep } from "./Ticket";

export function UpdateTicket({ action }: { action: QRreport.Action }) {

    type userData = {
        subject: string, 
        description: string,
    }

    const { register, handleSubmit, formState: { errors } } = useForm<userData>()

    const [fetchUrl, setFetchUrl] = useState('')
    const [init, setInit] = useState<RequestInit>({})

    const { isFetching, isCanceled, cancel, result, error } = useFetch<any>(fetchUrl, init)

    if (isFetching) return <Loading/>
    if (!isFetching && fetchUrl) {
        if (result?.body?.type === 'success') {
            return <TicketRep/>
        } else {
            return <DisplayError message={result?.body?.problem.title}/>
        }
    } 
    
    const onSubmitHandler = handleSubmit(({ subject, description }) => {
        const payload: any = {}
        
        payload['subject'] = subject !== '' ? subject : null
        payload['description'] = description !== '' ? description : null

        setInit({
            method: action.method,
            headers: {
                'Content-Type': action.type,
                'Request-Origin': 'WebApp' 
            },
            credentials: 'include',
            body: JSON.stringify(payload)
        })

        setFetchUrl(BASE_URL_API + action.href) 
    })
    
    function Inputs() {

        let componentsInputs = action.properties.map(prop => {
            switch (prop.name) {
                case 'subject': return <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/>
                case 'description': return <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/> 
            }
        })
        return <>{componentsInputs}</>
    }
    
    return (
        <section className="info-section">
            <div className="grid place-items-center">
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Update your account'>
                        <HeaderParagraph paragraph='Insert new credentials below'/>
                    </Header>
                    <Inputs/>
                    <SubmitButton text={'Update account'}/>
                    <Paragraph value = {'(*) Required'}/>
                </Form>
            </div>
        </section>
    )
}