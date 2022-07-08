import { useMemo } from "react"
import { Navigate } from "react-router-dom"
import { DisplayError } from "../Error"
import { useFetch } from "../hooks/useFetch"

type DeleteComponentProps = {
    urlToDelete: string,
    redirectUrl: string,
    setAction: React.Dispatch<React.SetStateAction<string>>,
}

export function DeleteComponent({urlToDelete, redirectUrl, setAction} : DeleteComponentProps) {
    const credentials: RequestInit = {
        method: 'DELETE',
        credentials: "include",
    }
    const init = useMemo(() => credentials ,[])
    
    const { isFetching, isCanceled, cancel, result, error } = useFetch<any>(urlToDelete, init)
    
    if (isFetching) {
        return <p>Deleting...</p>
    } else {
        if (result?.headers.status === 200) {
            return <Navigate to={redirectUrl}/>
        } else {
            return <DisplayError message={'Error deleting.'}/>
        }
    } 
}