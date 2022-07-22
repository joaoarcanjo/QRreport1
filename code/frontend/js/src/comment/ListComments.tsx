import { Action, Entity } from "../models/QRJsonModel"
import { getEntityLink, getEntityOrUndefined, getProblemOrUndefined, getSpecificEntity } from '../models/ModelUtils';
import { Collection, CollectionPagination } from "../pagination/CollectionPagination";
import { useMemo, useState } from "react";
import { GrUpdate }from "react-icons/gr";
import { AiFillDelete } from "react-icons/ai";
import { Outlet } from "react-router-dom";
import { Loading } from "../components/Various";
import { ErrorView } from "../errors/Error";
import { useFetch } from "../hooks/useFetch";
import { InsertCommentAction } from "./InsertComment";

function CommentAction({actions, setAction, setPayload}: {
    actions: Action[] | undefined,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
}) {

    const [auxAction, setAuxAction] = useState<Action | undefined>(undefined)

    if (!actions) return null

    const componentsActions = actions.map((action, idx) => {
        switch(action.name) {
            case 'update-comment': return (
                <button key={idx} onClick={() => setAuxAction!!(action)} className='bg-yellow-300 hover:bg-yellow-400 text-white rounded-lg text-sm px-2.5 py-1 inline-flex items-center mr-2 mb-2'>
                    <GrUpdate/>
                </button>
                )

            case 'delete-comment': return (
                <button key={idx} onClick={() => setAction!!(action)} className='bg-red-800 hover:bg-red-900 text-white rounded-lg text-sm px-2.5 py-1 inline-flex items-center mr-2 mb-2'>
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

function CommentsActions({actions, setAction, setPayload}: {
    actions: Action[] | undefined,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>,
}){

    const [auxAction, setAuxAction] = useState<Action>()
    
    if(!actions) return null;

    const componentsActions = actions?.map((action, idx) => {
        switch(action.name) {
            case 'create-comment': return (
                    <button key = {idx} onClick={() => setAuxAction(action)} className="text-white bg-blue-700 hover:bg-blue-800 rounded-lg px-2">
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

export function ListComments({collection, setAction, setPayload}: { 
    collection?: Entity<Collection>,
    setAction: React.Dispatch<React.SetStateAction<Action | undefined>>,
    setPayload: React.Dispatch<React.SetStateAction<string>>
}) {

    const initValues: RequestInit = {
        credentials: 'include',
        headers: { 'Request-Origin': 'WebApp' }
    }
    
    const init = useMemo(() => initValues ,[])

    const [currentUrl, setCurrentUrl] = useState('')
    const { isFetching, result, error } = useFetch<Collection>(currentUrl, init)
    
    if (isFetching) return <Loading/>
    if (error) return <ErrorView error={error}/>

    const problem = getProblemOrUndefined(result?.body)
    if (problem) return <ErrorView problemJson={problem}/>
    
    function CommentItem({entity}: {entity: Entity<any>}) {

        if (!entity) return null
        const comment = entity?.properties
        const actions = entity?.actions

        const author = getSpecificEntity(["person"], "comment-author", entity.entities)
        return (
            <div className='p-5 bg-white rounded-lg border border-gray-200 shadow-md'>
                <span className='text-l font-md text-gray-900'>{author?.properties.name}</span>
                <div className='space-y-2'>
                    <p className="text-sm text-slate-600">{comment.comment}</p>
                    <CommentAction actions={actions} setAction={setAction} setPayload={setPayload}/>
                </div>
                <p className='flex justify-end space-x-1 text-gray-500 text-sm'>
                    {`${new Date(comment.timestamp).toLocaleDateString()}`}
                </p>
            </div>
        )
    }

    if (result?.body) collection = getEntityOrUndefined(result.body)
    const comments = collection?.entities

    if (!comments || !collection) return null

    return (
        <div className="flex flex-col space-y-2">
            <h1 className='text-2xl mt-0 mb-2 text-blue-800'>Comments:</h1>
            <CommentsActions actions={collection.actions} setAction={setAction} setPayload={setPayload}/>
            <div className='space-y-3'>
                {comments.map((comment, idx) => <CommentItem key={idx} entity={comment}/>)}
            </div>
            <CollectionPagination 
                collection={collection.properties} 
                setUrlFunction={setCurrentUrl} 
                templateUrl={getEntityLink('pagination', collection)}/>
            <Outlet/>
        </div>
    )
}