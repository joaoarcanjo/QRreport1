package pt.isel.ps.project.integrationtests.comments

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import pt.isel.ps.project.integrationtests.comments.CommentExpectedRepresentations.CREATE_COMMENT
import pt.isel.ps.project.integrationtests.comments.CommentExpectedRepresentations.DELETE_COMMENT
import pt.isel.ps.project.integrationtests.comments.CommentExpectedRepresentations.GET_COMMENTS
import pt.isel.ps.project.integrationtests.comments.CommentExpectedRepresentations.UPDATE_COMMENT
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.ignoreTimestamp

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentTests {
    @Autowired
    private lateinit var client: TestRestTemplate

    @LocalServerPort
    private var port: Int = 0

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

    private final val headers = HttpHeaders().apply {
        add("Request-Origin", "Mobile")
        setBearerAuth(Utils.diogoAdminToken)
    }

    @Test
    fun `Get comments`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.Comments.makeBase(ticketId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

//        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_COMMENTS)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create comment`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.Comments.makeBase(ticketId)}"

        val comment = CreateCommentEntity("Comment test")

        val req = HttpEntity<String>(comment.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(CREATE_COMMENT)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `Update comment`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val commentId = 2L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.Comments.makeSpecific(ticketId, commentId)}"

        val comment = CreateCommentEntity("Comment update test")

        val req = HttpEntity<String>(comment.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(UPDATE_COMMENT)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Delete comment`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val commentId = 2L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.Comments.makeSpecific(ticketId, commentId)}"

        val res = client.exchange(url, HttpMethod.DELETE, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(DELETE_COMMENT)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}