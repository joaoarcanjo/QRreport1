package pt.isel.ps.project.responses

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.makePagination
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import java.net.URI

object Response {
    object Classes {
        const val COLLECTION = "collection"
        const val COMPANY = "company"
    }

    object Relations {
        const val SELF = "self"
        const val NEXT = "next"
        const val FIRST = "first"
        const val LAST = "last"
        const val PREV = "prev"
        const val ITEM = "item"
        const val AUTHOR = "author"
        const val COMPANIES = "companies"
        const val COMPANY_BUILDINGS = "company-buildings"
    }

    object Links {
        fun self(href: String) = QRreportJsonModel.Link(listOf(Relations.SELF), href)
        fun companies() = QRreportJsonModel.Link(listOf(Relations.COMPANIES), Uris.Companies.BASE_PATH)
    }

    fun buildResponse(
        representation: QRreportJsonModel,
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders = HttpHeaders(),
    ): ResponseEntity<QRreportJsonModel> {
        return ResponseEntity
            .status(status)
            .headers(headers)
            .body(representation)
    }

    fun setLocationHeader(uri: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.location = URI(uri)
        return headers
    }

    fun buildCollectionLinks(
        collection: CollectionModel,
        maxPageSize: Int,
        uri: String,
        otherLinks: List<QRreportJsonModel.Link>? = null
    ): MutableList<QRreportJsonModel.Link> {
        val links = mutableListOf<QRreportJsonModel.Link>()
        links.add(QRreportJsonModel.Link(listOf(Relations.SELF), makePagination(collection.pageIndex, uri)))
        if (collection.pageIndex * maxPageSize + maxPageSize <= collection.collectionSize) {
            links.add(QRreportJsonModel.Link(listOf(Relations.NEXT), makePagination(collection.pageIndex + 1, uri)))
            links.add(QRreportJsonModel.Link(listOf(Relations.LAST), makePagination(collection.collectionSize / maxPageSize, uri)))
        }
        if (collection.pageIndex > 1) {
            links.add(QRreportJsonModel.Link(listOf(Relations.PREV), makePagination(collection.pageIndex - 1, uri)))
            links.add(QRreportJsonModel.Link(listOf(Relations.FIRST), makePagination(0, uri)))
        }
        if (otherLinks != null) {
            links.addAll(otherLinks)
        }
        return links
    }
}
