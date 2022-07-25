import { useState, useEffect } from "react";
import Popup from "reactjs-popup";
import { ErrorView } from "../errors/Error";
import { ProblemJson } from "../models/ProblemJson";
import './../Popup.css'

export function ErrorPopup({error, problem, message}: {error?: Error, problem?: ProblemJson, message?: string}) {
    const [popup, setPopup] = useState(false)

    useEffect(() => {
        if (!problem) {
            setPopup(false)
            return
        } 
        setPopup(prev => !prev)
    }, [problem, setPopup])

    if (!problem && !message) return <></>
    
    return (
        <Popup className='popup-overlay' open={popup} onClose={() => setPopup(false)}>
            <ErrorView error={error} problemJson={problem} message={message}/>
        </Popup> 
    )
}