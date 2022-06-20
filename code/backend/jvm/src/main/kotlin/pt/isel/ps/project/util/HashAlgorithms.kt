package pt.isel.ps.project.util

import java.security.MessageDigest

object Hash {

    object SHA256 {
        const val HEXA_HASH_SIZE = 256 / 4 // The hash is apresented in hexadecimal

        private fun String.sha256(): String {
            val digest = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

        fun getHash(roomId: Long, deviceId: Long) = "$roomId$deviceId${System.currentTimeMillis()}".sha256()
    }

    object MD5 {
        const val HEXA_HASH_SIZE = 128 / 4 // The hash is apresented in hexadecimal

        private fun String.md5(): String {
            val digest = MessageDigest.getInstance("MD5").digest(this.toByteArray())
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

        fun getHash(roomId: Long, deviceId: Long) = "$roomId$deviceId${System.currentTimeMillis()}".md5()
    }
}