package com.miuidialer.app.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miuidialer.app.data.model.CallLogEntry
import com.miuidialer.app.data.model.Contact
import com.miuidialer.app.data.repository.CallLogRepository
import com.miuidialer.app.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactsRepository: ContactsRepository,
    private val callLogRepository: CallLogRepository
) : ViewModel() {

    private val _dialInput = MutableStateFlow("")
    val dialInput: StateFlow<String> = _dialInput.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _callLog = MutableStateFlow<List<CallLogEntry>>(emptyList())
    val callLog: StateFlow<List<CallLogEntry>> = _callLog.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredContacts = MutableStateFlow<List<Contact>>(emptyList())
    val filteredContacts: StateFlow<List<Contact>> = _filteredContacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCallLog()
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            _contacts.value = contactsRepository.getAllContacts()
            _filteredContacts.value = _contacts.value
            _isLoading.value = false
        }
    }

    fun loadCallLog() {
        viewModelScope.launch {
            _callLog.value = callLogRepository.getCallLog()
        }
    }

    fun onDialPadPress(digit: String) {
        _dialInput.value += digit
    }

    fun onDialPadDelete() {
        val current = _dialInput.value
        if (current.isNotEmpty()) {
            _dialInput.value = current.dropLast(1)
        }
    }

    fun onDialPadDeleteAll() {
        _dialInput.value = ""
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _filteredContacts.value = if (query.isEmpty()) {
                _contacts.value
            } else {
                contactsRepository.searchContacts(query)
            }
        }
    }

    fun makeCall(context: Context, number: String, simSlot: Int = 0) {
        val uri = Uri.fromParts("tel", number, null)
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        try {
            val intent = Intent(Intent.ACTION_CALL, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (simSlot == 1) {
                    putExtra("com.android.phone.extra.slot", 1)
                    putExtra("Asus.intent.extra.IS_MULTISIM", true)
                    putExtra("multisim_set_call_forward", 1)
                }
            }
            ContextCompat.startActivity(context, intent, null)
        } catch (e: SecurityException) {
            val intent = Intent(Intent.ACTION_DIAL, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            ContextCompat.startActivity(context, intent, null)
        }
    }

    fun setDialInput(number: String) {
        _dialInput.value = number
    }
}
