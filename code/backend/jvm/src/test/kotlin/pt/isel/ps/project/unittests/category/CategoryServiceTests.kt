package pt.isel.ps.project.unittests.category

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDto
import pt.isel.ps.project.model.category.CategoryItemDto
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.service.CategoryService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryServiceTests {
    @Autowired
    private lateinit var service: CategoryService

    @Autowired
    private lateinit var jdbi: Jdbi

    private val delScript = Utils.LoadScript.getResourceFile("sql/delete_tables.sql")
    private val fillScript = Utils.LoadScript.getResourceFile("sql/insert_tables_tests.sql")

    @BeforeEach
    fun setUp() {
        jdbi.open().use { h -> h.createScript(delScript).execute(); h.createScript(fillScript).execute() }
    }

    @AfterAll
    fun cleanUp() {
        jdbi.open().use { h -> h.createScript(delScript).execute();}
    }

    val adminUser = AuthPerson(
        UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"),
        "Diogo Novo",
        "961111111",
        "diogo@qrreport.com",
        "admin",
        null,
        listOf(LinkedHashMap<String, String>().apply {
            put("id", "1")
            put("name", "ISEL")
            put("state", "active")
            put("manages", listOf("1").toString())
        }),
        null,
        "active",
        null
    )

    @Test
    fun `Get categories default`() {
        val expectedCategories = CategoriesDto(
            listOf(
                CategoryItemDto(CategoryDto(1, "water", "active", null), true),
                CategoryItemDto(CategoryDto(2, "electricity", "active", null), true),
                CategoryItemDto(CategoryDto(3, "garden", "inactive", null), false),
                CategoryItemDto(CategoryDto(4, "window", "active", null), false),
            ),
            4)

        val categoriesDto = service.getCategories(DEFAULT_PAGE)

        Assertions.assertThat(categoriesDto.ignoreTimestamps()).isEqualTo(expectedCategories)
    }

    @Test
    fun `Create category`() {
        val categoryEntity = InputCategoryEntity("cleaning")
        val expectedCategory = CategoryItemDto(CategoryDto(5, "cleaning", "active", null), false)

        val categoryItemDto = service.createCategory(categoryEntity)

        Assertions.assertThat(categoryItemDto.ignoreTimestamp()).isEqualTo(expectedCategory)
    }

    @Test
    fun `Update category`() {
        val categoryId = 1L
        val categoryEntity = InputCategoryEntity("canalization")
        val expectedCategory = CategoryItemDto(
            CategoryDto(1, "canalization", "active", null),
            true
        )

        val categoryItemDto = service.updateCategory(categoryId, categoryEntity)

        Assertions.assertThat(categoryItemDto.ignoreTimestamp()).isEqualTo(expectedCategory)
    }

    @Test
    fun `Deactivate category`() {
        val categoryId = 4L
        val expectedCategory = CategoryItemDto(
            CategoryDto(4, "window", "inactive", null),
            false
        )

        val categoryItemDto = service.deactivateCategory(categoryId)

        Assertions.assertThat(categoryItemDto.ignoreTimestamp()).isEqualTo(expectedCategory)
    }

    @Test
    fun `Deactivate category that is being used throws error`() {
        val categoryId = 1L
        val expMsg = "ERROR: category-being-used"

        Assertions.assertThatThrownBy { service.deactivateCategory(categoryId) }
            .hasMessageContaining(expMsg)
    }

    @Test
    fun `Activate category`() {
        val categoryId = 3L
        val expectedCategory = CategoryItemDto(
            CategoryDto(3, "garden", "active", null),
            false
        )

        val categoryItemDto = service.activateCategory(categoryId)

        Assertions.assertThat(categoryItemDto.ignoreTimestamp()).isEqualTo(expectedCategory)
    }
}