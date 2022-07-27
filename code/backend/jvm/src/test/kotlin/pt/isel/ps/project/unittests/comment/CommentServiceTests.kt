package pt.isel.ps.project.unittests.comment

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.comment.CommentDto
import pt.isel.ps.project.model.comment.CommentItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.service.CommentService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentServiceTests {
    @Autowired
    private lateinit var service: CommentService

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
    fun `Get comments default`() {
        val ticketId = 1L
        val expectedComments = CommentsDto(
            listOf(
                CommentDto(
                    CommentItemDto(2, "Tente fazer o possível para estancar a fuga.", null),
                    PersonItemDto(
                        UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"),
                        "Diogo Novo",
                        "961111111",
                        "diogo@qrreport.com",
                        listOf("admin", "manager"),
                        null,
                        "active"
                    )
                ),
                CommentDto(
                    CommentItemDto(1, "Esta sanita não tem arranjo, vou precisar de uma nova.", null),
                    PersonItemDto(
                        UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"),
                        "Zé Manuel",
                        "965555555",
                        "zeze@fixings.com",
                        listOf("employee"),
                        listOf("electricity", "water"),
                        "active"
                    )
                ),
            ),
            2,
            "Fixing",
            false
        )

        val commentsDto = service.getComments(ticketId, DEFAULT_PAGE)
        commentsDto.comments?.map { comment -> Assertions.assertThat(expectedComments.comments?.contains(comment.ignoreTimestamp())).isEqualTo(true) }
        Assertions.assertThat(expectedComments.collectionSize).isEqualTo(commentsDto.collectionSize)
    }

    @Test
    fun `Create comment`() {
        val ticketId = 1L
        val commentEntity = CreateCommentEntity("Estamos à espera de novas peças")
        val expectedComment = CommentItemDto(3, commentEntity.comment, null)

        val anomalyItemDto = service.createComment(ticketId, commentEntity, adminUser)

        Assertions.assertThat(anomalyItemDto.ignoreTimestamp()).isEqualTo(expectedComment)
    }

    @Test
    fun `Update comment`() {
        val ticketId = 1L
        val commentId = 2L
        val commentEntity = CreateCommentEntity("Vamos encomendar peças novas!")
        val expectedComment = CommentItemDto(2, commentEntity.comment, null)

        val commentItemDto = service.updateComment(adminUser, ticketId, commentId, commentEntity)

        Assertions.assertThat(commentItemDto.ignoreTimestamp()).isEqualTo(expectedComment)
    }

    @Test
    fun `Delete comment`() {
        val ticketId = 1L
        val commentId = 2L
        val expectedComment =  CommentItemDto(2, "Tente fazer o possível para estancar a fuga.", null)

        val commentItemDto = service.deleteComment(adminUser, ticketId, commentId)

        Assertions.assertThat(commentItemDto.ignoreTimestamp()).isEqualTo(expectedComment)
    }
}