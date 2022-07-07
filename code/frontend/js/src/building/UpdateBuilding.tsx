import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, SubmitButton } from "../components/FormComponents";

export function UpdateBuilding() {

    type buildingData = {
        name: string,
        floors: number
    }

    const { register, handleSubmit, formState: { errors } } = useForm<buildingData>();

    const onSubmitHandler = handleSubmit(({ name }) => {
        console.log(name);
    })

    const nameInput: InputProps = {
        inputLabelName: 'New name',
        register: register('name', {minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: 'name',
        type: 'text',
        errorMessage: errors.name && 'Invalid name'
    }

    const floorsInput: InputProps = {
        inputLabelName: 'New number of floors',
        register: register('floors', {minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: 'floors',
        type: 'number',
        errorMessage: errors.name && 'Invalid floors number'
    }

    return (
        <section className='info-section'>
            <div className='space-y-3'>
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Update building'>
                        <HeaderParagraph paragraph='Insert new values below'/>
                    </Header>
                    <Input value = {nameInput}/>
                    <Input value = {floorsInput}/>
                    <SubmitButton text={'Update building'}/>
                </Form>
                <Paragraph value = {'(*) Required'}/>
            </div>
        </section>
    )
}