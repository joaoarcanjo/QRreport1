import { FaEnvelopeOpenText } from "react-icons/fa";

export function ThanksPage() {

    return (
        <div className="grid place-items-center space-y-4">
            <p className="mt-24 text-cyan-800 text-5xl font-bold">Thank you!</p>
            <FaEnvelopeOpenText style= {{ color: 'green', fontSize: "5em" }}/>
            <p className="text-cyan-800 text-xl">Your report will be analyzed!</p>
            <p className="text-cyan-800 text-sm">Keep reporting, your help is essential to us. </p>
        </div>
    )
}