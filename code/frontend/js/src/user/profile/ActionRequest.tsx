import { FormEventHandler, useMemo } from 'react';
import { Navigate } from 'react-router-dom';
import { Loading } from '../../components/Various';
import { DisplayError } from '../../Error';
import { useFetch } from '../../hooks/useFetch';
import * as QRreport from '../../models/QRJsonModel';
import { TicketRep } from '../../ticket/Ticket';
import { BASE_URL, BASE_URL_API } from '../../Urls';

type FormProps = {
    redirectUrl?: string,
    action: QRreport.Action, 
    extraInfo?: BodyInit,
    returnComponent?: React.ReactNode
}

export function ActionComponent({redirectUrl, action, extraInfo, returnComponent} : FormProps) {

    const credentials: RequestInit = {
        method: action.method,
        credentials: 'include',
        headers: {
            'Content-type': action.type,
            'Request-Origin': 'WebApp'
        }
    }

    if (extraInfo) credentials.body = extraInfo
    const init = useMemo(() => credentials ,[])
    const { isFetching, isCanceled, cancel, result, error } = useFetch<any>(BASE_URL_API + action.href, init)
    
    if (isFetching) {
        return <Loading/>
    } else {
        const status = result?.headers.status
        if (status === 200 || status === 201) {
            if(redirectUrl) {
                return <Navigate to={redirectUrl}/>
            }
            return <>{returnComponent}</>
        } else {
            return <DisplayError message={'Error deleting.'}/>
        }
    }
}