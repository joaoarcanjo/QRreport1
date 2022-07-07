import { Form, Header, HeaderParagraph, Input, InputProps, Options, OptionsProps, OptionsType, SubmitButton, TextArea, TextAreaProps } from '../components/FormComponents'
import { useForm } from 'react-hook-form'

type formTicketProps = {
    hash: String,
    possibleAnomalies?: OptionsType[]
}

export default function FormTicket({hash, possibleAnomalies}: formTicketProps) {
    
    type ticketData = { 
        subject: string, 
        description?: string, 
        anomaly: string,
        name: string, 
        phone: string
        email: string
    }

    const { register, watch, handleSubmit, formState: { errors }} = useForm<ticketData>();

    const anomalySelected = watch("anomaly", 'Other problem...');

    const onSubmitHandler = handleSubmit(({ subject , description, anomaly, name, phone, email }) => {
        const payload: any = {}
        
        if (description !== undefined && anomaly === 'other value' && description.length !== 0) {
            payload['description'] = description
        }
        if (phone !== undefined && phone.length !== 0) {
            payload['phone'] = phone
        }
        payload['subject'] = anomaly === 'other value' ? subject : anomaly
        payload['hash'] = hash
        payload['name'] = name
        payload['email'] = email

        console.log(payload)
        fetch("http://localhost:8080/v1/tickets", {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)})
            .then(response => console.log(response.status))
    })

    const anomaliesOptions: OptionsProps = {
        register: register("anomaly", { required: true }),
        options: possibleAnomalies
    }

    const subjectInput: InputProps = {
        inputLabelName: 'Subject *',
        register: register("subject", {minLength: 0, maxLength: 50}),
        style: {borderColor: errors.subject ? 'red': 'black'},
        name: "subject",
        type: "text",
        errorMessage: errors.subject && 'Invalid subject'
    }
 
    const textAreaValues: TextAreaProps = {
        textAreaLabelName: 'Description',
        register: register("description", { maxLength: 200 }),
        style: {borderColor: errors.description ? 'red': 'black'},
        text: 'Explain the problem',
        errorMessage: errors.description && 'Invalid description'
    }
  
    const emailInput: InputProps = {
        inputLabelName: 'Email *',
        register: register("email", {required: 'Is required', pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.email ? 'red': 'black'},
        name: "email",
        type: "email",
        errorMessage: errors.email && 'Invalid email'
    }

    const nameInput: InputProps = {
        inputLabelName: 'Your name *',
        register: register("name", {required: 'Is required', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }

    const phoneNumberInput: InputProps = {
        inputLabelName: 'Phone number',
        register: register("phone", { minLength: 1, maxLength: 50}),
        style: {borderColor: errors.phone ? 'red': 'black'},
        name: "phone",
        type: "tel",
        errorMessage: errors.phone && 'Invalid phone number'
    }

    /**
     * Text area will just appear when the anomalie option is "Other problem"
     */
    return (
        <section className='info-section'>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Header heading='Welcome to QRreport'>
                    <HeaderParagraph paragraph='Tell us more about the problem found'/>
                    <HeaderParagraph paragraph='Building: A | Room: 1 - Bathroom'/>
                </Header>
                <Options value={anomaliesOptions}/>
                {(anomalySelected === 'other value') && <Input value={subjectInput}/>}
                {(anomalySelected === 'other value') && <TextArea value={textAreaValues}/>}
                <Input value={nameInput}/> 
                <Input value={emailInput}/>
                <Input value={phoneNumberInput}/> 
                <SubmitButton text={'Submit'}/>
                <p> (*) Required</p>
            </Form>
        </section>
    )
}