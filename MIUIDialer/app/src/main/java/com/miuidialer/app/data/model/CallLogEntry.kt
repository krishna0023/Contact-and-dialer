package com.miuidialer.app.data.model

data class CallLogEntry(
    val id: Long,
    val number: String,
    val name: String?,
    val type: CallType,
    val date: Long,
    val duration: Long,
    val simSlot: Int = 0
)

enum class CallType {
    INCOMING, OUTGOING, MISSED, REJECTED
}
