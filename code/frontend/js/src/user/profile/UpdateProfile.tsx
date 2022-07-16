import { useState } from "react";
import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, Paragraph, SubmitButton } from "../../components/form/FormComponents";
import { emailInputForm, passwordInputForm, passwordVerifyInputForm, phoneInputForm, simpleInputForm } from "../../components/form/FormInputs";
import { Loading } from "../../components/Various";
import { ErrorView } from "../../errors/Error";
import { useFetch } from "../../hooks/useFetch";
import * as QRreport from '../../models/QRJsonModel';
import { BASE_URL_API } from "../../Urls";
import { Profile } from "./Profile";
import { getProblemOrUndefined } from "../../models/ModelUtils"

export function UpdateProfile({ action }: { action: QRreport.Action }) {

    type userData = {
        name: string, 
        email: string,
        phone: string,
        password: string,
        passwordVerify: string
    }

    const { register, handleSubmit, formState: { errors }, getValues } = useForm<userData>()

    const [fetchUrl, setFetchUrl] = useState('')
    const [init, setInit] = useState<RequestInit>({})

    const { isFetching, result, error } = useFetch<any>(fetchUrl, init)

    if (isFetching) return <Loading/>

    if (!isFetching && fetchUrl) {
        if (error) return <ErrorView error={error}/>
        else if (result?.body?.type === 'success') return <Profile/>
        else return <ErrorView problemJson={getProblemOrUndefined(result?.body)}/>
    } 
    
    const onSubmitHandler = handleSubmit(({ name, email, phone, password, passwordVerify }) => {
        const payload: any = {}

        payload['name'] = name !== '' ? name : null
        payload['email'] = email !== '' ? email : null
        payload['phone'] = phone !== '' ? phone : null
        payload['password'] = password !== '' ? password : null
        payload['passwordVerify'] = passwordVerify !== '' ? passwordVerify : null

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
                case 'name': return <Input value={simpleInputForm(register, errors, prop.required, prop.name, prop.type)}/>
                case 'phone': return <Input value={phoneInputForm(register, errors, prop.required, prop.name)}/>
                case 'email': return <Input value={emailInputForm(register, errors, prop.required, prop.name)}/>
                case 'password': return (
                    <>
                        <Input value={passwordInputForm(register, errors, prop.required, prop.name)}/>
                        <Input value={passwordVerifyInputForm(register, errors, getValues, prop.required)}/>
                    </>)
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