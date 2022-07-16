import { useParams } from 'react-router-dom'
import { useMemo, useState } from 'react'
import { CompanyRep } from '../../company/Company'
import { useFetch } from '../../hooks/useFetch'
import { FormInfo } from '../../models/Models'
import { Action } from '../../models/QRJsonModel'
import { REPORT_FORM_URL_API } from '../../Urls'
import { ActionComponent } from '../../components/ActionComponent'
import { Loading } from '../../components/Various'
import { ErrorView } from '../../errors/Error'
import { getAction, getActionsOrUndefined, getEntityOrUndefined } from "../../models/ModelUtils"
import { TicketInfo } from './TicketForm'

export function TicketRequest() {

    const { hash } = useParams()

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')

    const { isFetching, isCanceled, cancel, result, error } = useFetch<FormInfo>(REPORT_FORM_URL_API(hash!!), init)
    
    switch (action?.name) {
        case 'report': return <ActionComponent action={action} extraInfo={payload} returnComponent={<CompanyRep/>} />
        case 'signup': return <ActionComponent action={action} extraInfo={payload} returnComponent={<CompanyRep/>} />
        case 'login': return <ActionComponent action={action} extraInfo={payload} returnComponent={<CompanyRep/>} />
    }
    
    if (isFetching) return <Loading/>
    if (isCanceled) return <p>Canceled</p>
    if (error) return <ErrorView error={error}/>

    const reportAction = getAction('report', result?.body)
    const reportEntity = getEntityOrUndefined(result?.body)

    if(!reportAction || !reportEntity) return null

    return <TicketInfo hash={hash!!} entity={reportEntity} action={reportAction} setAction={setAction} setPayload={setPayload}/>
}