import { useMemo } from 'react';
import { Navigate } from 'react-router-dom';
import { DisplayError } from '../Error';
import { useFetch } from '../hooks/useFetch';
import * as QRreport from '../models/QRJsonModel';
import { BASE_URL, BASE_URL_API } from '../Urls';
import { Profile } from './Profile';

type DeleteComponentProps = {
    redirectUrl?: string,
    action: QRreport.Action, 
    extraInfo?: BodyInit
}

export function ActionComponent({redirectUrl, action, extraInfo} : DeleteComponentProps) {
    
    console.log(`Extra info: ${extraInfo}`)
    const credentials: RequestInit = {
        method: action.method,
        credentials: 'include',
        headers: {
            'Content-type': action.type,
            'Request-Origin': 'WebApp'
        }
    }
    if (extraInfo) credentials.body = extraInfo
    
    console.log(credentials)
    const init = useMemo(() => credentials ,[])
    
    const { isFetching, isCanceled, cancel, result, error } = useFetch<any>(BASE_URL_API + action.href, init)
    
    if (isFetching) {
        return <p>Executing...</p>
    } else {
        if (result?.headers.status === 200) {
            if(redirectUrl) {
                return <Navigate to={redirectUrl}/>
            }
            return <Profile/> 
        } else {
            return <DisplayError message={'Error deleting.'}/>
        }
    } 
}