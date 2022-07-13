import { Action, Entity } from "../models/QRJsonModel"
import { getSpecificEntity } from '../models/ModelUtils';
import { FaRegSadTear } from "react-icons/fa";
import { Collection } from "../pagination/CollectionPagination";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { simpleTextAreaForm } from "../components/form/FormInputs";
import { Form, TextArea } from "../components/form/FormComponents";
import { MdExpandLess, MdExpandMore } from "react-icons/md";
import { GrUpdate }from "react-icons/gr";
import { AiFillCloseCircle, AiFillDelete } from "react-icons/ai";

export function ListComments({entity, setAction, setPayload}: { 
    entity: Entity<Collection> | undefined,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    if (!entity || !setAction || !setPayload) return null
    const comments = entity.entities
    if(!comments) return null

    function CommentAction({actions}: {actions: Action[] | undefined}) {

        const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

        if (!actions) return null

        const componentsActions = actions.map(action => {
            switch(action.name) {
                case 'update-comment': return (
                    <button onClick={() => setAuxAction!!(action)} className='bg-yellow-300 hover:bg-yellow-400 text-white rounded-lg text-sm px-2.5 py-1 inline-flex items-center mr-2 mb-2'>
                        <GrUpdate/>
                    </button>
                    )

                case 'delete-comment': return (
                    <button onClick={() => setAction!!(action)} className='bg-red-800 hover:bg-red-900 text-white rounded-lg text-sm px-2.5 py-1 inline-flex items-center mr-2 mb-2'>
                        <AiFillDelete/>
                    </button>
                )
            }
        })

        return (
            <>
                <div className='flex justify-end items-center'>{componentsActions} </div>
                {auxAction?.name === 'update-comment' && 
                <InsertCommentAction action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
            </>
        )
    }

    function CommentItem({entity}: {entity: Entity<any>}) {

        if (!entity) return null
        const comment = entity?.properties
        const actions = entity?.actions

        const author = getSpecificEntity(["person"], "comment-author", entity.entities)
        return (
            <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md'>
                <span className='text-l font-md text-gray-900'>{author?.properties.name}</span>
                <p className="text-sm text-slate-600">{comment.comment}</p>
                <CommentAction actions={actions}/>
            </div>
        )
    }
    
    function InsertCommentAction({ action, setAction, setPayload, setAuxAction }: { 
        action: Action,
        setAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined,
        setPayload: React.Dispatch<React.SetStateAction<string>> | undefined,
        setAuxAction: React.Dispatch<React.SetStateAction<Action | undefined>> | undefined
    }) {
        type commentData = { comment: string }

        const { register, handleSubmit, formState: { errors } } = useForm<commentData>()
        
        if (!action || !setAction || !setPayload || !setAuxAction) return null;

        const onSubmitHandler = handleSubmit(({ comment }) => {
            setPayload(JSON.stringify({comment: comment}))
            setAction(action)
        })

        function Inputs() {
            let componentsInputs = action.properties.map(prop => {
                switch (prop.name) {
                    case 'comment': return <TextArea value={simpleTextAreaForm(register, errors, prop.required, prop.name, '')}/>
                }
            })
            return <>{componentsInputs}</>
        }
        
        return(
            <div className="space-y-3 p-5 bg-green rounded-lg border border-gray-200">
                <button onClick={() => setAuxAction(undefined)}>
                    <AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "1.4em" }}/>
                </button>
                <Form onSubmitHandler = { onSubmitHandler }>
                    <Inputs/>
                    <button className="text-white bg-green-500 hover:bg-green-700 rounded-lg px-2">
                        Submit comment
                    </button>
                </Form>
            </div> 
        )
    }

    function CommentsActions({ actions }: {actions: Action[] | undefined}){

        const [auxAction, setAuxAction] = useState<Action>()
        
        if(!actions) return null;

        const componentsActions = actions?.map(action => {
            switch(action.name) {
                case 'create-comment': return (
                        <button onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
                            {action.title}
                        </button>
                    )
            }
        })

        return (
            <>
                <div className="flex space-x-2">{componentsActions} </div>
                {auxAction?.name === 'create-comment' && 
                <InsertCommentAction action={auxAction} setAction={setAction} setPayload={setPayload} setAuxAction={setAuxAction}/>}
            </>
        )
    }

    return (
        <div className="flex flex-col space-y-2">
            <h1 className='text-2xl mt-0 mb-2 text-blue-800'>Comments:</h1>
            <div>
                {
                    comments.length === 0 ? (
                        <div className='flex items-center space-x-2'>
                            <span>
                                Don't found any comment 
                            </span>
                            <FaRegSadTear/>
                        </div>
                    ): (
                        <div className='space-y-3'>
                            {comments.map((comment, idx) => <CommentItem key={idx} entity={comment}/>)}
                        </div>
                    )
                }
            </div>
            <CommentsActions actions={entity.actions}/>
        </div>
    )
}