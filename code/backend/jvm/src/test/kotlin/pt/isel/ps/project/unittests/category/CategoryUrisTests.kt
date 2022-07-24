package pt.isel.ps.project.unittests.category

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION

class CategoryUrisTests {

    @Test
    fun `Make valid category specific path`() {
        val categoryId = 123L
        val expectedPath = "$VERSION/categories/123"

        val path = Uris.Categories.makeSpecific(categoryId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid category activate path`() {
        val categoryId = 123L
        val expectedPath = "$VERSION/categories/123/activate"

        val path = Uris.Categories.makeActivate(categoryId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid category deactivate path`() {
        val categoryId = 123L
        val expectedPath = "$VERSION/categories/123/activate"

        val path = Uris.Categories.makeActivate(categoryId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}