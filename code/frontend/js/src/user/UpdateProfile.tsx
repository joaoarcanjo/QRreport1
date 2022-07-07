import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, SubmitButton } from "../components/FormComponents";

export function UpdateProfile() {

    type userData = {
        name: string, 
        email: string,
        phone: string,
        password: string,
        passwordVerify: string
    }

    const { register, handleSubmit, watch, formState: { errors }, getValues } = useForm<userData>();

    const passwordInserted = watch("password");

    const onSubmitHandler = handleSubmit(({ email, password }) => {
        console.log(email, password);
    })

    const nameInput: InputProps = {
        inputLabelName: 'New name',
        register: register("name", {minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }

    const emailInput: InputProps = {
        inputLabelName: 'New email',
        register: register("email", {pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.email ? 'red': 'black'},
        name: "email",
        type: "email",
        errorMessage: errors.email && 'Invalid email'
    }

    const passwordInput: InputProps = {
        inputLabelName: 'New password',
        register: register("password", {minLength: 1, maxLength: 127}),
        style: {borderColor: errors.password ? 'red': 'black'},
        name: "password",
        type: "password",
        errorMessage: errors.password && 'Invalid password'
    }

    const passwordVerifyInput: InputProps = {
        inputLabelName: 'Repeat your password *',
        register: register("passwordVerify", {minLength: 1, maxLength: 127, validate: value => (value === getValues("password") && getValues("password") !== '')}),
        style: {borderColor: errors.passwordVerify ? 'red': 'black'},
        name: "passwordVerify",
        type: "password",
        errorMessage: errors.passwordVerify && 'Verify if both passwords are equal.'
    }


    const phoneNumberInput: InputProps = {
        inputLabelName: 'New phone number',
        register: register("phone", { minLength: 1, maxLength: 50}),
        style: {borderColor: errors.phone ? 'red': 'black'},
        name: "phone",
        type: "tel",
        errorMessage: errors.phone && 'Invalid phone number'
    }
    
    return (
        <section className="info-section">
            <div className="space-y-3">
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Update your account'>
                        <HeaderParagraph paragraph='Insert new credentials below'/>
                    </Header>
                    <Input value = {nameInput}/>
                    <Input value = {emailInput}/>
                    <Input value = {phoneNumberInput}/>
                    <Input value = {passwordInput}/>
                    {(passwordInserted !== undefined && passwordInserted !== '') && <Input value = {passwordVerifyInput}/>}
                    <SubmitButton text={'Update account'}/>
                </Form>
                <Paragraph value = {'(*) Required'}/>
            </div>
        </section>
    )
}