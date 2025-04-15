package com.example.stajh2test.db

/**
 * Data class representing a user in the application
 */
data class User(
    val code: String,          // Application specific code
    val login: String,         // Username for login
    val email: String,         // Email address for authentication
    val password: String       // Password (not stored in Firestore directly)
) {
    /**
     * Convert to a Map for Firestore storage (excluding password)
     */
    fun toMap(): Map<String, Any> = mapOf(
        "code" to code,
        "login" to login,
        "email" to email
    )

    companion object {
        /**
         * Create User from Firestore data (without password)
         */
        fun fromMap(map: Map<String, Any>, password: String = ""): User {
            return User(
                code = map["code"] as? String ?: "",
                login = map["login"] as? String ?: "",
                email = map["email"] as? String ?: "",
                password = password
            )
        }
    }
}