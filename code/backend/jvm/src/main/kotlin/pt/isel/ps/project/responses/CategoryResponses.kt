package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDtoItem
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader

object CategoryResponses {
    const val CATEGORY_PAGE_MAX_SIZE = 10

    object Actions {
        fun createCategory() = QRreportJsonModel.Action(
            name = "create-category",
            title = "Create a category",
            method = HttpMethod.POST,
            href = Uris.Categories.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string", required = true)
            )
        )

        fun updateCategory(categoryId: Long) = QRreportJsonModel.Action(
            name = "update-category",
            title = "Update category",
            method = HttpMethod.PUT,
            href = Uris.Categories.makeSpecific(categoryId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string", required = true)
            )
        )

        fun activateCategory(categoryId: Long) = QRreportJsonModel.Action(
            name = "activate-category",
            title = "Activate category",
            method = HttpMethod.PUT,
            href = Uris.Categories.makeSpecific(categoryId)
        )

        fun deactivateCategory(categoryId: Long) = QRreportJsonModel.Action(
            name = "deactivate-category",
            title = "Deactivate category",
            method = HttpMethod.PUT,
            href = Uris.Categories.makeSpecific(categoryId)
        )
    }

    private fun getCategoryItem(category: CategoryDtoItem, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Response.Classes.CATEGORY),
        rel = rel,
        properties = category,
        actions = listOf(
            Actions.deactivateCategory(category.id),
            Actions.updateCategory(category.id),
            Actions.activateCategory(category.id)), //todo: adicionar à representação
        links = listOf()
    )

    fun getCategoriesRepresentation(categoriesDto: CategoriesDto, collection: CollectionModel) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.CATEGORY, Classes.COLLECTION),
            properties = collection,
            entities = mutableListOf<QRreportJsonModel>().apply {
                if (categoriesDto.categories != null)
                    addAll(categoriesDto.categories.map { getCategoryItem(it, listOf(Relations.ITEM)) })
            },
            actions = listOf(Actions.createCategory()),
            links = listOf(Links.self(Uris.Categories.BASE_PATH))
        )
    )

    fun createCategoryRepresentation(category: CategoryDtoItem) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.CATEGORY),
            properties = category,
            links = listOf(Links.self(Uris.Categories.makeSpecific(category.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Categories.makeSpecific(category.id))
    )

    fun updateCategoryRepresentation(category: CategoryDtoItem) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.CATEGORY),
            properties = category,
            links = listOf(Links.self(Uris.Categories.makeSpecific(category.id)))
        )
    )

    fun deactivateCategoryRepresentation(category: CategoryDtoItem) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.CATEGORY),
            properties = category,
            links = listOf(Links.self(Uris.Categories.makeSpecific(category.id)), Links.categories())
        )
    )

    fun activateCategoryRepresentation(category: CategoryDtoItem) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.CATEGORY),
            properties = category,
            links = listOf(Links.self(Uris.Categories.makeSpecific(category.id)))
        )
    )
}