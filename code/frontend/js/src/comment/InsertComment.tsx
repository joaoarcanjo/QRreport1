import { useForm } from "react-hook-form";
import { AiFillCloseCircle } from "react-icons/ai";
import { TextArea, Form, LittleSubmitButton } from "../components/form/FormComponents";
import { simpleTextAreaForm } from "../components/form/FormInputs";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";

export function InsertCommentAction({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>
}) {
    type commentData = { comment: string }

    const { register, handleSubmit, formState: { errors } } = useForm<commentData>()

    const onSubmitHandler = handleSubmit(({ comment }) => {
        setPayload(JSON.stringify({comment: comment}))
        setAction(action)
    })

    function Inputs() {
        let componentsInputs = action.properties.map((prop, idx) => {
            switch (prop.name) {
                case 'comment': return <TextArea key={idx} value={simpleTextAreaForm('Comment', register, errors, prop.required, prop.name, 'Write here')}/>
            }
        })
        return <>{componentsInputs}</>
    }
    
    return(
        <div className="space-y-3 p-5 bg-white bg-green rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div> 
    )
}