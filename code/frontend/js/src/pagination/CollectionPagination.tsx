import { ReactElement } from "react"
import { Link } from "../models/QRJsonModel"
import { BASE_URL_API } from "../Urls"

export type Collection = {
    pageIndex: number,
    pageMaxSize: number,
    collectionSize: number,
}

const selectedColor = 'bg-blue-600 hover:bg-blue-800'
const nonSelectedColor = 'bg-gray-400 hover:bg-gray-600'
const rightRounded = 'rounded-r-lg'
const leftRounded = 'rounded-l-lg'
const rounded = 'rounded-lg'

export function CollectionPagination({ collection, setUrlFunction, templateUrl }
    : { 
        collection?: any, 
        setUrlFunction: React.Dispatch<React.SetStateAction<string>>,
        templateUrl: Link | undefined
    }
) {
    if (!collection) return null

    function handleOnClick(id: number) {
        if(templateUrl?.templated) {
            const newUrl = templateUrl.href.replace('{?page}', `?page=${id}`)
            setUrlFunction(BASE_URL_API + newUrl)
        }
    }

    const pagination: ReactElement[] = []
    const totalPages = Math.ceil(collection.collectionSize / collection.pageMaxSize)
    const currentPage: number = collection.pageIndex

    function selectRounded(page: number) {
        switch (page) {
            case 1: {
                if(page === totalPages) 
                    return rounded
                if(page === currentPage) 
                    return leftRounded
                break
            }
            case totalPages: {
                if(page === currentPage) 
                    return rightRounded
                break
            }
        } return ''
    }

    for (let page = 1; page <= totalPages; page++) {
        const color = page === currentPage ? selectedColor : nonSelectedColor
        const rounded = selectRounded(page)
        pagination.push(
        <button className= {`h-7 text-white ${color} ${rounded} px-3`} key={page} onClick={() => handleOnClick(page)} > {page} </button>)
    }

    return (
        <div className="flex items-center justify-center">
            {currentPage === 1 ? null : 
                <>
                    <button className= {`h-7 text-white ${nonSelectedColor} rounded-l-lg px-3`} onClick={() => handleOnClick(1)}>&laquo;</button>
                    <button className={`h-7 text-white ${nonSelectedColor} px-3`} onClick={() => handleOnClick(currentPage - 1)}>&lt;</button>
                </>}
            {pagination}
            {currentPage === totalPages ? null : 
                <>
                    <button className={`h-7 text-white ${nonSelectedColor} px-3`} onClick={() => handleOnClick(currentPage + 1)}>&gt;</button> 
                    <button className={`h-7 text-white ${nonSelectedColor} rounded-r-lg px-3`} onClick={() => handleOnClick(totalPages)}>&raquo;</button>
                </>}
        </div>
    )
}
