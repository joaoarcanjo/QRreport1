import { useState } from "react";
import { AiFillStar } from "react-icons/ai";
import { Action } from "../models/QRJsonModel";

export function TicketRate({action, setPayload, setAction}: {
    action: Action | undefined,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {
    const [starsCounter, setStarCount] = useState<number>(0)
    
    if(!action || !setAction || !setPayload) return null

    const stars = Array(5).fill('').map((x, idx) => {return (
            <button key={idx} onClick={() => setStarCount(idx + 1)}>
                <AiFillStar style= {{ color: starsCounter <= idx ? '#686762' : '#e5b215', fontSize: "1.4em" }}/>
            </button>
        )
    })

    return (
        <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200 shadow-md bg-gradient-to-r from-green-100 to-green-300">
            <div className="grid place-items-center">
                <p>The report was concluded!</p>
                <span>Give us your opinion:</span>
            </div>
            <div className="grid place-items-center space-y-2">
                <div className="flex items-center">
                    {stars}
                </div>
                <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2"
                        onClick={() => {setPayload(JSON.stringify({rate: starsCounter})); setAction(action)}}>
                    Submit rating
                </button>
            </div>
        </div>
    )
}