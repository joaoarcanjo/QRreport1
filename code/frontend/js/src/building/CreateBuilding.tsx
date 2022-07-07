import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, SubmitButton } from "../components/FormComponents";

export function CreateBuilding({companyId}: {companyId: number}) {

    type companyData = {
        name: string,
        floors: number,
        managerEmail: string
    }

    const { register, handleSubmit, formState: { errors } } = useForm<companyData>();

    const onSubmitHandler = handleSubmit(({ name }) => {
        console.log(name);
    })
    
    const nameInput: InputProps = {
        inputLabelName: 'Building name *',
        register: register("name", {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }

    const floorsInput: InputProps = {
        inputLabelName: 'Number of floors *',
        register: register('floors', {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: 'floors',
        type: 'number',
        errorMessage: errors.name && 'Invalid floors number'
    }

    const managerInput: InputProps = {
        inputLabelName: 'Manager email *',
        register: register("managerEmail", {required: 'Is required', pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "manager",
        type: "email",
        errorMessage: errors.name && 'Invalid manager email'
    }
    
    return (
        <section className="info-section">
            <div className="space-y-3">
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Create new building'>
                        <HeaderParagraph paragraph='Insert building info below'/>
                    </Header>
                    <Input value = {nameInput}/>
                    <Input value = {floorsInput}/>
                    <Input value = {managerInput}/>
                    <SubmitButton text={'Create building'}/>
                </Form>
                <Paragraph value = {'(*) Required'}/>
            </div>
        </section>
    )
}