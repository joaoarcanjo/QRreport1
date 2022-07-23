import { useState, useMemo } from "react"
import { CloseButton } from "../../components/Various"
import { Company } from "../../models/Models"
import { Action } from "../../models/QRJsonModel"
import { SelectUserCompany } from "./SelectUserCompany"

export function RehireAction({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
}) {

    const [ company, setCompany ] = useState<Company | undefined>(undefined)

    const onSubmitHandler = () => {
        if(!company) return
        action.href = action.href.replace('{companyId}', company.id.toString())
        setAction(action)
    }
    
    const selectCompany = useMemo(() => {
        return <SelectUserCompany action={action} setPayload={setCompany} setAction={undefined} setAuxAction={undefined} />
    },[])

    return (
        <div className="space-y-1 p-3 bg-white rounded-lg border border-gray-200 shadow-md">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <p>Company selected: {company === undefined ? '-----' : `${company.name}`}</p>
            {selectCompany}
            <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2" onClick={onSubmitHandler}>
                {action.title}
            </button>
        </div>)
}