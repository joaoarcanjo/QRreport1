import { useForm } from "react-hook-form";
import { Form, Header, HeaderParagraph, Input, InputProps, Paragraph, SubmitButton } from "../components/form/FormComponents";

export function CreateRoom({buildingId}: {buildingId: number}) {

    type roomData = {
        name: string,
        floor: number
    }

    const { register, handleSubmit, formState: { errors } } = useForm<roomData>();

    const onSubmitHandler = handleSubmit(({ name, floor }) => {
        console.log(name, floor);
    })
    
    const nameInput: InputProps = {
        inputLabelName: 'Building name *',
        register: register("name", {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }

    const floorInput: InputProps = {
        inputLabelName: 'Room floor number *',
        register: register('floor', {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: 'floors',
        type: 'number',
        errorMessage: errors.name && 'Invalid floor number'
    }
    
    return (
        <section className="info-section">
            <div className="space-y-3">
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Header heading='Create new room'>
                        <HeaderParagraph paragraph='Insert room info below'/>
                    </Header>
                    <Input value = {nameInput}/>
                    <Input value = {floorInput}/>
                    <SubmitButton text={'Create room'}/>
                </Form>
                <Paragraph value = {'(*) Required'}/>
            </div>
        </section>
    )
}