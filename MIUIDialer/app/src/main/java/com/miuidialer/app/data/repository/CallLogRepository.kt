package com.miuidialer.app.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import com.miuidialer.app.data.model.CallLogEntry
import com.miuidialer.app.data.model.CallType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallLogRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun getCallLog(limit: Int = 100): List<CallLogEntry> = withContext(Dispatchers.IO) {
        val entries = mutableListOf<CallLogEntry>()
        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.PHONE_ACCOUNT_ID
            ),
            null, null,
            "${CallLog.Calls.DATE} DESC LIMIT $limit"
        )
        cursor?.use {
            val idIdx = it.getColumnIndex(CallLog.Calls._ID)
            val numIdx = it.getColumnIndex(CallLog.Calls.NUMBER)
            val nameIdx = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val typeIdx = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIdx = it.getColumnIndex(CallLog.Calls.DATE)
            val durIdx = it.getColumnIndex(CallLog.Calls.DURATION)
            while (it.moveToNext()) {
                val rawType = it.getInt(typeIdx)
                val callType = when (rawType) {
                    CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                    CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                    CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                    CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
                    else -> CallType.INCOMING
                }
                entries.add(
                    CallLogEntry(
                        id = it.getLong(idIdx),
                        number = it.getString(numIdx) ?: "",
                        name = it.getString(nameIdx),
                        type = callType,
                        date = it.getLong(dateIdx),
                        duration = it.getLong(durIdx)
                    )
                )
            }
        }
        entries
    }
}
