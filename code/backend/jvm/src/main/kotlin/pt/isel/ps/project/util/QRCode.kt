package pt.isel.ps.project.util

import io.github.g0dkar.qrcode.QRCode
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object QRCode {
    fun generate(content: String): ByteArrayResource {
        val imageData = QRCode(content).render(cellSize = 10, margin = 10)
        val imageBytes = ByteArrayOutputStream().also { ImageIO.write(imageData, "PNG", it) }.toByteArray()
        return ByteArrayResource(imageBytes, MediaType.IMAGE_PNG_VALUE)
    }
}