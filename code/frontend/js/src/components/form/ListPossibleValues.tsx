import { useMemo } from "react"
import { DisplayError } from "../../Error"
import { useFetch } from "../../hooks/useFetch"
import { getEntitiesOrUndefined } from "../../models/ModelUtils"
import { Collection } from "../../pagination/CollectionPagination"
import { BASE_URL_API } from "../../Urls"
import { Loading } from "../Various"
import { Options } from "./FormComponents"

export function ListPossibleValues({ register, regName, href, listText, otherValueText }: {
    register: any, 
    regName: string,
    href: string | null | undefined
    listText: string,
    otherValueText?: string
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }

    const init = useMemo(() => initValues ,[])
    const url = href === undefined || null ? '' : BASE_URL_API + href
    
    const { isFetching, isCanceled, cancel, result, error } = useFetch<Collection>(url, init)

    if (isFetching) return <Loading/>
    if (isCanceled) return <>Canceled</> //todo
    if (error) return <DisplayError/>

    const options = getEntitiesOrUndefined(result?.body)?.map(current => {
        return {label: current.properties.id, value: current.properties.name}
    })

    return <Options value={{
        optionsText: listText, 
        otherValueText: otherValueText, 
        register: register(regName), 
        options: options, 
        defaultOtherValue: -1
    }}/>
}