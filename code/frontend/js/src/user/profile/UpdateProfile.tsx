import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, Paragraph, BigSubmitButton, LittleSubmitButton } from "../../components/form/FormComponents";
import { emailInputForm, passwordInputForm, passwordVerifyInputForm, phoneInputForm, simpleInputForm } from "../../components/form/FormInputs";
import { CloseButton } from "../../components/Various";
import { Action } from "../../models/QRJsonModel";

export function UpdateProfile({action, setAction, setAuxAction, setPayload }: {  
    action?: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type userData = {
        name: string, 
        email: string,
        phone: string,
        password: string,
        passwordVerify: string
    }

    const { register, handleSubmit, formState: { errors }, getValues } = useForm<userData>()

    if(!action) return null
    
    const onSubmitHandler = handleSubmit(({ name, email, phone, password, passwordVerify }) => {
        const payload: any = {}

        if(!name && !email && !phone && ! password && ! passwordVerify) return

        payload['name'] = name !== '' ? name : null
        payload['email'] = email !== '' ? email : null
        payload['phone'] = phone !== '' ? phone : null
        payload['password'] = password !== '' ? password : null
        payload['passwordVerify'] = passwordVerify !== '' ? passwordVerify : null
        
        console.log("HELOO")
        setAction(action)
        setPayload(JSON.stringify(payload))

    })
    
    function Inputs() {

        let componentsInputs = action!!.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'name': return <Input key={idx} value={simpleInputForm(register, 'Name', errors, prop.required, prop.name, prop.type)}/>
                case 'phone': return <Input key={idx} value={phoneInputForm(register, errors, prop.required, prop.name)}/>
                case 'email': return <Input key={idx} value={emailInputForm(register, errors, prop.required, prop.name)}/>
                case 'password': return (
                    <div key={idx}>
                        <Input key={idx} value={passwordInputForm(register, errors, prop.required, prop.name)}/>
                        <Input key={idx + 1} value={passwordVerifyInputForm(register, errors, getValues, prop.required)}/>
                    </div>)
            }
        })
        return <>{componentsInputs}</>
    }

    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <Form onSubmitHandler = { onSubmitHandler }>
                <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
                <Inputs/>
                <Paragraph value = {'(*) Required'}/>
                <LittleSubmitButton text={action.title}/>
            </Form>
        </div>
            
    )
}