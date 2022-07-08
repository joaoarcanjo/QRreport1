import { ReactElement } from "react"
import './Pagination.css'

export type Collection = {
    pageIndex: number,
    pageMaxSize: number,
    collectionSize: number,
}

export function CollectionPagination({ collection, setPage }
    : { 
        collection?: any, 
        setPage: React.Dispatch<React.SetStateAction<number>>,
    }
) {
    if (!collection) return null

    function handleOnClick(id: number) {
        setPage(id)
    }

    const pagination: ReactElement[] = []
    const totalPages = Math.ceil(collection.collectionSize / collection.pageMaxSize)
    const currentPage = collection.pageIndex

    for (let page = 0; page < totalPages; page++) {
        pagination.push(
            <button key={page} onClick={() => handleOnClick(page)} className={page === currentPage ? "active": ""}>{page + 1}</button>
        )
    }

    return (
        <div className='pagination pl-20 pt-5'>
            {currentPage === 0 ? null : 
                <>
                    <button onClick={() => handleOnClick(0)}>&laquo;</button>
                    <button onClick={() => handleOnClick(currentPage - 1)}>&lt;</button>
                </>}
            {pagination}
            {currentPage === totalPages - 1 ? null : 
                <>
                    <button onClick={() => handleOnClick(currentPage + 1)}>&gt;</button>
                    <button onClick={() => handleOnClick(totalPages - 1)}>&raquo;</button>
                </>}
        </div>
    )
}
