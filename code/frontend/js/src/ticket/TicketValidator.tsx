import { useParams } from "react-router-dom";
import { OptionsType } from "../components/FormComponents";
import FormTicket from "./TicketForm";

export default function FormValidator() {

    console.log("Form validator called.")

    const { hash } = useParams()
    console.log(`Hash value: ${hash}`)

    /*
    Use fetch to send a request to the server to validate if hash is valid and return the possible anomalies.
        -> If hash is not valid, return a error component.
        -> If hash is valid, return form ticket.
    */

    //mock values
    const possibleAnomalies: OptionsType[] = [
        {'label': 'torneira a pingar', 'value': 'Torneira avariada'},
        {'label': 'torneira só sai agua suja', 'value': 'Torneira só sai agua suja'},
        {'label': 'torneira toda cagada', 'value': 'Torneira suja'},
    ]

    return <FormTicket hash = {hash!!} possibleAnomalies={possibleAnomalies} />
}