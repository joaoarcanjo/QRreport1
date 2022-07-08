import { useState } from "react";
import { MdExpandLess, MdExpandMore } from "react-icons/md";
import { Link } from "react-router-dom";
import { State, Ticket, Comment } from "../Models";

export function TicketRep() {

    function UpdateState({states}: {states: State[]}) {

        const [currentState, setState] = useState<State>()
        return (
            <>
                <div className="space-x-2 space-y-2">
                    <p>Select new state:</p>
                    {Array.from(states).map(state => 
                        <button className='bg-blue-400 hover:bg-blue-800 text-white rounded px-1'
                                onClick= {() => setState(state)}>
                            {state.name}
                        </button>
                    )}
                </div>
                {currentState !== undefined ? 
                <button className='w-full bg-green-500 hover:bg-green-600 text-white rounded px-1 py-1'>
                    Change state to {currentState.name}
                </button> : <></>}
            </>
        )
    }

    function TicketInfo({ticket}: {ticket: Ticket}) {

        const [updateFLag, setUpdateFlag] = useState(false)

        return (
            <>
                <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                    <p className='text-gray-900 font-bold text-xl leading-8 my-1'>{ticket.subject}</p>
                    <div className='flex flex-col space-y-4'>
                        <p> Building {ticket.buildingName} - Room {ticket.roomName} </p>
                        <p> {ticket.category} </p>
                        <p> {ticket.description} </p>
                    </div>
                    <button className='bg-blue-400 hover:bg-blue-600 text-white py-1 px-3 rounded inline-flex items-center' 
                            onClick={() => setUpdateFlag(!updateFLag)}> Update state
                        {!updateFLag && <MdExpandMore style= {{ color: 'white', fontSize: '2em' }} />}
                        { updateFLag && <MdExpandLess style= {{ color: 'white', fontSize: '2em' }} />}
                    </button>
                    {updateFLag && <UpdateState states={ticket.possibleTransitions}/>}
                </div>
            </>
        )
    }

    function CommentItem({comment}: {comment: Comment}) {
        return (
            <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md'>  
                <h5 className='text-xl font-md text-gray-900'>{comment.authorName}</h5>
                <p>{comment.comment}</p>
            </div>
        )
    }

    function Comments({comments}: {comments: Comment[]}) {
        return (
            <div className='flex flex-col space-y-3'>
                {Array.from(comments).map((comment, idx) => <CommentItem key={idx} comment={comment}/>)}
            </div>
        )
    }

    const mockTicket = {
        'id': 1,
        'subject': 'Torneira suja',
        'description': 'Some description',
        'category': 'Electricity',
        'buildingName': 'A', //falta retornar
        'roomName': '1',     //falta retornar
        'possibleTransitions': [
            {'id': 1, 'name': 'Fixing'},
            {'id': 2, 'name': 'Pause'},
            {'id': 3, 'name': 'Waiting for material'},
            {'id': 4, 'name': 'Concluded'}
        ]
    }

    const mockValues = [
        {'id': 1, 'authorName': 'André', 'comment': 'Fui ali e já venho'},
        {'id': 1, 'authorName': 'Alfredo', 'comment': 'Fui ali e já venho'},
        {'id': 1, 'authorName': 'Ricardo', 'comment': 'Fui ali e já venho'}
    ]

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <TicketInfo ticket={mockTicket}/>
            <Comments comments={mockValues}/>
            <div className='flex space-x-4'>
                <Link className='w-1/2' to={`/deliveTicket/${mockTicket.id}`}>
                    <button className='w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded'>
                        Delive ticket
                    </button>
                </Link>
                <button className='w-1/2 bg-red-700 hover:bg-red-900 text-white font-bold py-2 px-4 rounded'>
                    Delete ticket
                </button>
            </div>
        </div>
    )
}