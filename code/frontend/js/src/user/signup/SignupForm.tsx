import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, SubmitButton } from "../../components/form/FormComponents";

export default function SignupForm() {

    type signupData = {
        name: string, 
        email: string,
        phone: string,
        password: string,
        passwordVerify: string
    }

    const { register, handleSubmit, formState: { errors }, getValues } = useForm<signupData>();

    const onSubmitHandler = handleSubmit(({ email, password }) => {
        console.log(email, password);
    })

    
    const nameInput: InputProps = {
        inputLabelName: 'Your name *',
        register: register("name", {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }

    const emailInput: InputProps = {
        inputLabelName: 'Email *',
        register: register("email", {required: 'Is required', pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.email ? 'red': 'black'},
        name: "email",
        type: "email",
        errorMessage: errors.email && 'Invalid email'
    }

    const passwordInput: InputProps = {
        inputLabelName: 'Password *',
        register: register("password", {required: 'Is required', minLength: 1, maxLength: 127}),
        style: {borderColor: errors.password ? 'red': 'black'},
        name: "password",
        type: "password",
        errorMessage: errors.password && 'Invalid password'
    }

    const passwordVerifyInput: InputProps = {
        inputLabelName: 'Repeat your password *',
        register: register("passwordVerify", {required: 'Is required', minLength: 1, maxLength: 127, validate: value => value === getValues("password")}),
        style: {borderColor: errors.passwordVerify ? 'red': 'black'},
        name: "passwordVerify",
        type: "password",
        errorMessage: errors.passwordVerify && 'Verify if both passwords are equal.'
    }


    const phoneNumberInput: InputProps = {
        inputLabelName: 'Phone number',
        register: register("phone", { minLength: 1, maxLength: 50}),
        style: {borderColor: errors.phone ? 'red': 'black'},
        name: "phone",
        type: "tel",
        errorMessage: errors.phone && 'Invalid phone number'
    }
    
    return (
        <section className="info-section">
            <div className="space-y-3 grid h-screen place-items-center">
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Welcome to QRreport'>
                        <HeaderParagraph paragraph='Insert your credentials below'/>
                    </Header>
                    <Input value = {nameInput}/>
                    <Input value = {emailInput}/>
                    <Input value = {phoneNumberInput}/>
                    <Input value = {passwordInput}/>
                    <Input value = {passwordVerifyInput}/>
                    <SubmitButton text={'Create account'}/>
                    <Paragraph value = {'(*) Required'}/>
                </Form>
            </div>
        </section>
    )
}