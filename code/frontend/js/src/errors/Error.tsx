import { AiOutlineWarning } from "react-icons/ai";
import { useNavigate } from "react-router-dom"
import { ProblemJson, InvalidParameters } from "../models/ProblemJson"

export function ErrorView({error, message, problemJson} : {
    error?: Error, 
    message?: string,
    problemJson?: ProblemJson
}) {
    const navigate = useNavigate(); 

    function ProblemComponent() {
        if(!problemJson) return <></>
        
        return(
            <div>
                <TitleProblemSection title={problemJson.title}/>
                <p>{problemJson.detail}</p>
                <InvalidParameters invalidParameters={problemJson.invalidParams}/>
            </div>
        )
    }

    function MessageInfo() {
        let errorMessage: string
        if (error !== undefined) errorMessage = error.message
        else if (message !== undefined) errorMessage = message
        else errorMessage = 'Unexpected error occurred, try again later.'
        return <p>{errorMessage}</p>
    }

    function TitleProblemSection({title}: {title: string}) {
        return <p className="text-base font-light leading-relaxed mt-0 mb-4 text-grey-600">{title}</p>
    }

    function InvalidParameters({invalidParameters}: {invalidParameters: InvalidParameters[] | undefined}) {
        if(!invalidParameters) return <></>

        const parametersComps = invalidParameters.map(parameter => {
            return (
                <div className="p-2 bg-white rounded-lg border border-gray-200 divide-y space-y-2">
                <p>{`Parameter: ${parameter.name}`}</p>
                <p className="p-1 text-sm text-gray-600">{`Local: ${parameter.local}`}</p>
                <p className="p-1 text-sm text-gray-600">{`Reason: ${parameter.reason}`}</p>
                </div>
            )
        })
        
        return parametersComps ? (
            <div className="p-2 bg-white rounded-lg border border-gray-500 space-y-2">
                <p>Invalid Parameters:</p>
                {parametersComps}
            </div>
        ): <></>
    }

    //window.location.reload()
    function ReturnHomeButton() {
        return (
            <button onClick={() => navigate('/')} className="focus:outline-none text-white bg-blue-500 hover:bg-blue-700 px-2 py-1 font-basic rounded-lg">
                Return home
            </button>
        )
    }

    return (
        <div className="px-3 pt-3 space-y-4">
            <div className="flex text-xl bg-clip-text text-transparent bg-gradient-to-r from-red-500 to-red-900 font-bold ">
                <AiOutlineWarning style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
                <span>Error</span>
                <AiOutlineWarning style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
            </div>
            {!problemJson && <MessageInfo/>}
            <ProblemComponent/>
            <ReturnHomeButton/>
        </div>
    )
}