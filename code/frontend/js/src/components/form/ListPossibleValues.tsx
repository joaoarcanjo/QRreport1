import { useMemo } from "react"
import { ErrorView } from "../../errors/Error"
import { useFetch } from "../../hooks/useFetch"
import { getEntitiesOrUndefined, getProblemOrUndefined } from "../../models/ModelUtils"
import { Collection } from "../../pagination/CollectionPagination"
import { BASE_URL_API } from "../../Urls"
import { Loading } from "../Various"
import { Options } from "./FormComponents"

export const LIST_DEFAULT_VALUE = '-1'

export function ListPossibleValues({ register, regName, href, listText, otherValueText, setValue }: {
    register: any, 
    regName: string,
    href: string | null | undefined
    listText: string,
    otherValueText?: string, 
    setValue?: React.Dispatch<React.SetStateAction<string>>
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const url = href === undefined || null ? '' : BASE_URL_API + href
    console.log(url)
    const { isFetching, result, error } = useFetch<Collection>(url, init)

    if (isFetching) return <Loading/>
    if (error) return <ErrorView />

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>

    const options = getEntitiesOrUndefined(result?.body)?.map(current => {
        if(regName === 'anomaly') { //TODO: EVITAR ESTE IF 
            return {label: current.properties.anomaly, value: current.properties.anomaly}
        } else {
            return {label: current.properties.id, value: current.properties.name}
        }
    })

    
    return <Options setValue={setValue} value={{
        optionsText: listText, 
        otherValueText: otherValueText, 
        register: register(regName), 
        options: options, 
        defaultOtherValue: otherValueText ? LIST_DEFAULT_VALUE : undefined,
    }}/>
}