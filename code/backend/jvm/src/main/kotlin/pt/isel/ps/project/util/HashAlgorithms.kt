package pt.isel.ps.project.util

import java.security.MessageDigest

object Hash {

    object SHA256 {

        const val HASH_SIZE = 256/4

        fun String.hashValue(): String {
            val bytes = this.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }
    }
}