package com.byteutility.dev.quickfill.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.quickfill.data.local.Snippet
import com.byteutility.dev.quickfill.data.repository.SnippetRepository
import com.byteutility.dev.quickfill.service.BaseSlotTileService
import com.byteutility.dev.quickfill.util.ClipboardHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: SnippetRepository
) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    val snippets: StateFlow<List<Snippet>> = repository.getAllSnippets()
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                list.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun copySnippet(snippet: Snippet) {
        ClipboardHelper.copyToClipboard(
            context = getApplication(),
            label = snippet.title,
            text = snippet.content,
            showToast = true
        )
    }

    fun saveSnippet(
        id: Long = 0,
        title: String,
        content: String,
        quickSlot: Int = 0,
        iconName: String = "clipboard"
    ) {
        viewModelScope.launch {
            val snippet = Snippet(
                id = id,
                title = title.trim(),
                content = content.trim(),
                quickSlot = quickSlot,
                iconName = iconName
            )
            val savedId = repository.saveSnippet(snippet)
            if (quickSlot in 1..3) {
                repository.assignQuickSlot(if (id != 0L) id else savedId, quickSlot)
            }
            BaseSlotTileService.requestAllTileUpdates(getApplication())
        }
    }

    fun deleteSnippet(snippet: Snippet) {
        viewModelScope.launch {
            repository.deleteSnippet(snippet)
            BaseSlotTileService.requestAllTileUpdates(getApplication())
        }
    }

    fun togglePin(snippet: Snippet) {
        viewModelScope.launch {
            repository.togglePin(snippet)
        }
    }

    fun assignQuickSlot(snippet: Snippet, slot: Int) {
        viewModelScope.launch {
            if (snippet.quickSlot == slot) {
                repository.clearQuickSlot(slot)
                repository.saveSnippet(snippet.copy(quickSlot = 0))
            } else {
                repository.assignQuickSlot(snippet.id, slot)
                repository.saveSnippet(snippet.copy(quickSlot = slot))
            }
            BaseSlotTileService.requestAllTileUpdates(getApplication())
        }
    }
}
