package com.miuidialer.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miuidialer.app.data.model.Contact
import com.miuidialer.app.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filtered = MutableStateFlow<List<Contact>>(emptyList())
    val filtered: StateFlow<List<Contact>> = _filtered.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            val all = contactsRepository.getAllContacts()
            _contacts.value = all
            _filtered.value = all
            _isLoading.value = false
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _filtered.value = if (query.isEmpty()) _contacts.value
            else contactsRepository.searchContacts(query)
        }
    }

    fun addContact(name: String, phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = contactsRepository.addContact(name, phone)
            if (result) loadContacts()
            onResult(result)
        }
    }
}
