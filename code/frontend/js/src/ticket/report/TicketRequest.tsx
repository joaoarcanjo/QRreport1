import { useParams } from 'react-router-dom'
import { useMemo, useState } from 'react'
import { useFetch } from '../../hooks/useFetch'
import { FormInfo } from '../../models/Models'
import { Action } from '../../models/QRJsonModel'
import { REPORT_FORM_URL_API } from '../../Urls'
import { ActionComponent } from '../../components/ActionComponent'
import { Loading } from '../../components/Various'
import { ErrorView } from '../../errors/Error'
import { getAction, getEntityOrUndefined } from "../../models/ModelUtils"
import { TicketForm } from './TicketForm'
import { useLoggedInState } from '../../user/Session'

export function TicketRequest() {

    const { hash } = useParams()
    const loggedState = useLoggedInState()
    let headersVal: HeadersInit | undefined
    if (loggedState?.isLoggedIn)
    headersVal = { 'Request-Origin': 'WebApp' }
    const initValues: RequestInit = {
        credentials: 'include',
        headers: headersVal
    }
    
    const init = useMemo(() => initValues ,[])
    const [action, setAction] = useState<Action | undefined>(undefined)
    const [payload, setPayload] = useState('')

    const { isFetching, result, error } = useFetch<FormInfo>(REPORT_FORM_URL_API(hash!!), init)
    
    const isLoggedIn = loggedState?.isLoggedIn

    switch (action?.name) {
        case 'report': return <ActionComponent action={action} extraInfo={payload} redirectUrl={isLoggedIn ? '/tickets': '/'} />
        case 'signup': return <ActionComponent action={action} extraInfo={payload} returnComponent={<></>} />
        case 'login': return <ActionComponent action={action} extraInfo={payload} returnComponent={<></>} />
    }
    
    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const reportAction = getAction('report', result?.body)
    const reportEntity = getEntityOrUndefined(result?.body)

    if(!reportAction || !reportEntity) return null

    return <TicketForm hash={hash!!} entity={reportEntity} action={reportAction} setAction={setAction} setPayload={setPayload}/>
}