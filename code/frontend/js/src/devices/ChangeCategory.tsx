import { useMemo, useState } from "react";
import { SelectCategory } from "../category/SelectCategory";
import { CloseButton } from "../components/Various";
import { Category } from "../models/Models";
import { Action } from "../models/QRJsonModel";

export function ChangeCategory({action, setAction, setAuxAction, setPayload }: {  
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    const [ category, setCategory ] = useState<Category | undefined>(undefined)

    const onSubmitHandler = () => {
        if(!category) return
        setAction(action)
        setPayload(JSON.stringify({newCategoryId: category.id}))
    }

    const categoryInput = useMemo(() => {
        return <SelectCategory action={action} setPayload={setCategory} setAuxAction={undefined} propName={"category"}/>
    }, [action])
    
    return (
        <div className="space-y-3 p-5 bg-white rounded-lg border border-gray-200">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <p>Category selected: {category === undefined ? '-----' : `${category.name}`}</p>
            {categoryInput}
            <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2" onClick={onSubmitHandler}>
                {action.title}
            </button>
        </div> 
    )
}