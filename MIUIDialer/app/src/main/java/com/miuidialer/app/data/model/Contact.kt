package com.miuidialer.app.data.model

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumbers: List<PhoneNumber>,
    val photoUri: String? = null,
    val isFavorite: Boolean = false
)

data class PhoneNumber(
    val number: String,
    val type: String
)
