package pt.isel.ps.project.model.representations

const val DEFAULT_PAGE = 1
const val QUERY_PAGE_KEY = "page"
const val DEFAULT_SORT = "date"
const val QUERY_SORT_KEY = "sort_by"
const val DEFAULT_DIRECTION = "desc"
const val QUERY_DIRECTION_KEY = "direction"
const val QUERY_SEARCH_KEY = "search"

data class CollectionModel(
    val pageIndex: Int,
    val pageMaxSize: Int,
    val collectionSize: Int,
)

data class PaginationEntity(
    val limit: Int,
    var offset: Int,
)

data class PaginationDto(
    val page: Int,
    val limit: Int,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_LIMIT = 10
    }
}

fun elemsToSkip(pageIdx: Int, pageSize: Int) = (pageIdx - 1) * pageSize
fun PaginationDto.toEntity() = PaginationEntity(limit, page * limit)
