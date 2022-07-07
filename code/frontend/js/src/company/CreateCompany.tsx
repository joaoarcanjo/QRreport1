import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, SubmitButton } from "../components/FormComponents";

export function CreateCompany() {

    type companyData = {
        name: string
    }

    const { register, handleSubmit, formState: { errors } } = useForm<companyData>();

    const onSubmitHandler = handleSubmit(({ name }) => {
        console.log(name);
    })
    
    const nameInput: InputProps = {
        inputLabelName: 'Company name *',
        register: register("name", {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }
    
    return (
        <section className="info-section">
            <div className="space-y-3">
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Create new company'>
                        <HeaderParagraph paragraph='Insert company name below'/>
                    </Header>
                    <Input value = {nameInput}/>
                    <SubmitButton text={'Create company'}/>
                </Form>
                <Paragraph value = {'(*) Required'}/>
            </div>
        </section>
    )
}