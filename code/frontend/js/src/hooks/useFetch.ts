import { useEffect, useReducer } from "react"
import * as QrJson from '../models/QRJsonModel'
import { ProblemJson } from "../models/ProblemJson"

export enum MediaType { 
    DAW_JSON = 'application/vnd.qrreport+json',
    PROBLEM_JSON = 'application/problem+json',
}

type ResponseHeader = Pick<Response, 'headers' | 'status' | 'statusText' | 'type' | 'url' | 'ok'>

export type FetchResult<T> = 
    | { type: 'success', entity: QrJson.Entity<T> }
    | { type: 'problem', problem: ProblemJson }

/**
 * Characterizes results for API requests.
 * @param headers  - the response's header information.
 * @param body     - the result body, if it exists.
 */
export type Result<T> = {
    headers: ResponseHeader,
    body?: FetchResult<T>
}

type State<T> = {
    isFetching: boolean,
    isCanceled: boolean,
    cancel?: () => void,
    result?: Result<T>,
    error?: Error,
}

export enum States { 
    FETCH_STARTED = 'fetch-started', 
    ERROR = 'error',
    RESPONSE = 'response',
    PAYLOAD = 'payload',
    RESET = 'reset',
    CANCEL = 'cancel',
}

type Action<T> =
    | { type: States.FETCH_STARTED, url: string }
    | { type: States.ERROR, error: Error }
    | { type: States.RESPONSE, responseHeaders: ResponseHeader }
    | { type: States.PAYLOAD, payload: FetchResult<T> }
    | { type: States.RESET }
    | { type: States.CANCEL }

function reducer<T>(state: State<T>, action: Action<T>): State<T> {
    switch (action.type) {
        case States.FETCH_STARTED: return { ...state, isFetching: true }
        case States.ERROR: return { ...state, isFetching: false, error: action.error }
        case States.RESPONSE: return { ...state, isFetching: true, result: { headers: action.responseHeaders } }
        case States.PAYLOAD: return { ...state, isFetching: false, result: { headers: state.result!!.headers, body: action.payload } }
        case States.RESET: return { isFetching: false, isCanceled: false }
        case States.CANCEL: return { ...state, isFetching: false, isCanceled: true }
    }
}

export function mapToFetchResult<T>(payload: any, contentType: string | null): FetchResult<T> {
    switch (contentType) {
        case MediaType.DAW_JSON: return { type: 'success', entity: payload }
        case MediaType.PROBLEM_JSON: return { type: 'problem', problem: payload }
    }
    throw new Error('Empty payload or content-type not expected.')
}

async function doFetch<T>(url: string, dispatcher: (action: Action<T>) => void, init?: RequestInit) {
    if (url === '') {
        dispatcher({ type: States.RESET })
        return
    }
    dispatcher({ type: States.FETCH_STARTED, url: url })
    try {
        const response = await fetch(url, {...init})
        dispatcher({ type: States.RESPONSE, responseHeaders: response })
        const payload = await response.json()
        dispatcher({ type: States.PAYLOAD, payload: mapToFetchResult(payload, response.headers.get('content-type')) })
    } catch (error) {
        dispatcher({ type: States.ERROR, error: error as Error }) 
    }
}

export function useFetch<T>(url: string, init?: RequestInit): State<T> {
    const [state, dispatcher] = useReducer(reducer, { isFetching: false, isCanceled: false })
    useEffect(
        () => { 
            doFetch(url, dispatcher, init)
        }
        ,[url, init, dispatcher]
    )
    console.log(state)
    return state as State<T>
}
