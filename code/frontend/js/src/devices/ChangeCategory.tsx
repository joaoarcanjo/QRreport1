import { useForm } from "react-hook-form";
import { AiFillCloseCircle } from "react-icons/ai";
import { Form, LittleSubmitButton } from "../components/form/FormComponents";
import { ListPossibleValues } from "../components/form/ListPossibleValues";
import { CloseButton } from "../components/Various";
import { Action } from "../models/QRJsonModel";

export function ChangeCategory({action, setAction, setAuxAction, setPayload }: {  
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    type deviceCategoryData = {
        category: number
    }

    const { register, handleSubmit } = useForm<deviceCategoryData>();

    const onSubmitHandler = handleSubmit(({ category }) => {
        setAction(action)
        setPayload(JSON.stringify({newCategoryId: category}))
    })

    function Inputs() {
        let componentsInputs = action!!.properties.map((prop, idx) => {
            console.log(prop);
            switch (prop.name) {
                case 'category': return <ListPossibleValues key={idx}
                    register={register} regName={prop.name} href={prop.possibleValues?.href} listText={'Select new category'}/>
            }
        })
        return <>{componentsInputs}</>
    }
    
    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <Form onSubmitHandler = { onSubmitHandler }>
                <Inputs/>
                <LittleSubmitButton text={`${action.title}`}/>
            </Form>
        </div> 
    )
}