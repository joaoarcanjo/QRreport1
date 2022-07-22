import { useState, useEffect } from "react";
import Popup from "reactjs-popup";
import { ErrorView } from "../errors/Error";
import { ProblemJson } from "../models/ProblemJson";
import './../Popup.css'

export function ErrorPopup({problem, message}: {problem?: ProblemJson, message?: string}) {
    const [popup, setPopup] = useState(false)

    useEffect(() => {
        if (!problem) {
            setPopup(false)
            return
        } 
        setPopup(prev => !prev)
    }, [problem, setPopup])

    if (!problem && !message) return <></>
    console.log('boas')
    return (
        <Popup className='popup-overlay' open={popup} onClose={() => setPopup(false)}>
            <ErrorView problemJson={problem} message={message}/>
        </Popup> 
    )
}