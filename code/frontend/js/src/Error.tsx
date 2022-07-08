import { MouseEventHandler, ReactElement, useState } from "react"
import { Navigate } from "react-router-dom"

export function InputError({error} : {error : string | undefined}) {
    return <p className="text-sm text-red-500"> {error} </p>
}

function ReturnButton({buttonName, onClick} : {buttonName: string, onClick: MouseEventHandler}) {
    return <button onClick={onClick} className="focus:outline-none text-white bg-blue-500 hover:bg-blue-700 px-2 py-1 font-basic rounded-lg">{buttonName}</button>
}

type DisplayErrorProps = {
    error?: Error,
    message?: string
}

export function DisplayError({error, message} : DisplayErrorProps) {

    const [returnHome, setReturnHome] = useState(false)

    function returnFunction() {
        setReturnHome(true)
    }

    function MessageInfo() {
        let errorMessage: string
        if (error !== undefined) errorMessage = error.message
        else if (message !== undefined) errorMessage = message
        else errorMessage = 'Unexpected error occurred, try again later.'
        return <p>{errorMessage}</p>
    }

    if (returnHome) return <Navigate to={'/'}/>

    return (
        <div className="py-10 px-10">
            <div className="w-80 bg-white p-6 rounded-t shadow-2xl shadow-red-300">
                <div className="flex-col space-y-3 mb-4 text-center">
                    <div className=" text-xl bg-clip-text text-transparent bg-gradient-to-r from-red-500 to-red-900 font-bold ">
                        Error!
                    </div>
                    <MessageInfo/>
                    <ReturnButton buttonName={'Return home'} onClick={returnFunction}/>
                </div>
            </div>  
        </div>
    )
}