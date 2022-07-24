import { useMemo } from 'react';
import { Navigate } from 'react-router-dom';
import { Loading } from './Various';
import { ErrorView } from '../errors/Error';
import { useFetch } from '../hooks/useFetch';
import * as QRreport from '../models/QRJsonModel';
import { BASE_URL_API } from '../Urls';
import { getProblemOrUndefined } from "../models/ModelUtils"
import { useLoggedInState } from '../user/Session';

type FormProps = {
    redirectUrl?: string,
    action: QRreport.Action, 
    extraInfo?: BodyInit,
    returnComponent?: React.ReactNode,
}

export function ActionComponent({redirectUrl, action, extraInfo, returnComponent} : FormProps) {
    const loggedState = useLoggedInState()

    let headersVal: HeadersInit | undefined = { 'Content-type': action.type }
    if (loggedState?.isLoggedIn) headersVal["Request-Origin"] = 'WebApp'

    const credentials: RequestInit = {
        method: action.method,
        credentials: 'include',
        headers: headersVal
    }
    console.log(credentials);
    
    if (extraInfo) credentials.body = extraInfo
    const init = useMemo(() => credentials, [])
    const { isFetching, result, error } = useFetch<any>(BASE_URL_API + action.href, init)
    if (isFetching) {
        return <Loading/>
    } else {

        const problem = getProblemOrUndefined(result?.body)
        if (problem) {
            return <ErrorView problemJson={problem}/>
        }

        const status = result?.headers.status
        if (status === 200 || status === 201) {
            if(redirectUrl) {
                return <Navigate to={redirectUrl}/>
            }
            return <>{returnComponent}</>
        } 
        if (error) {
            return <ErrorView error={error}/>
        }
        return <></>
    } 
}