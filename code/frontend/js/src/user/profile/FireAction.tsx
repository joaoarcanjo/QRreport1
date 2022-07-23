import { useMemo, useState } from "react"
import { useForm } from "react-hook-form"
import { Form, LittleSubmitButton, TextArea } from "../../components/form/FormComponents"
import { simpleTextAreaForm } from "../../components/form/FormInputs"
import { CloseButton } from "../../components/Various"
import { Company } from "../../models/Models"
import { Action } from "../../models/QRJsonModel"
import { SelectUserCompany } from "./SelectUserCompany"

export function FireAction({ action, setAction, setPayload, setAuxAction }: { 
    action: Action,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
    setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
}) {
    type roleData = { 
        company: number,
        reason: string 
    }

    const [ company, setCompany ] = useState<Company | undefined>(undefined)

    const { register, handleSubmit, formState: { errors } } = useForm<roleData>()

    const onSubmitHandler = handleSubmit(({ reason }) => {
        if(!company) return
        action.href = action.href.replace('{companyId}', company.id.toString())
        setAction(action)
        setPayload(JSON.stringify({reason: reason}))
    })
    
    const selectCompany = useMemo(() => {
        return <SelectUserCompany action={action} setPayload={setCompany} setAction={undefined} setAuxAction={undefined} />
    },[])

    let componentsInputs = action.properties.map((prop, idx) => {
        switch (prop.name) {
            case 'reason': return <TextArea key={idx} value={simpleTextAreaForm('Reason', register, errors, prop.required,  prop.name, 'Insert the reason')}/>
        }
    })

    return (
        <div className="space-y-1 p-3 bg-white rounded-lg border border-gray-200 shadow-md">
            <CloseButton onClickHandler={() => setAuxAction(undefined)}/>
            <p>Company selected: {company === undefined ? '-----' : `${company.name}`}</p>
            {selectCompany}
            {componentsInputs}
            <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2" onClick={onSubmitHandler}>
                {action.title}
            </button>
        </div>)
}