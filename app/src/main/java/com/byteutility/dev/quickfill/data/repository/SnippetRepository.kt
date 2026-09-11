package com.byteutility.dev.quickfill.data.repository

import com.byteutility.dev.quickfill.data.local.Snippet
import com.byteutility.dev.quickfill.data.local.SnippetDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface SnippetRepository {
    fun getAllSnippets(): Flow<List<Snippet>>
    fun searchSnippets(query: String): Flow<List<Snippet>>
    suspend fun getSnippetBySlot(slot: Int): Snippet?
    fun observeSnippetBySlot(slot: Int): Flow<Snippet?>
    suspend fun getSnippetById(id: Long): Snippet?
    suspend fun saveSnippet(snippet: Snippet): Long
    suspend fun deleteSnippet(snippet: Snippet)
    suspend fun togglePin(snippet: Snippet)
    suspend fun assignQuickSlot(snippetId: Long, slot: Int)
    suspend fun clearQuickSlot(slot: Int)
}

@Singleton
class DefaultSnippetRepository @Inject constructor(
    private val snippetDao: SnippetDao
) : SnippetRepository {

    override fun getAllSnippets(): Flow<List<Snippet>> = snippetDao.getAllSnippets()

    override fun searchSnippets(query: String): Flow<List<Snippet>> =
        if (query.isBlank()) snippetDao.getAllSnippets() else snippetDao.searchSnippets(query)

    override suspend fun getSnippetBySlot(slot: Int): Snippet? = snippetDao.getSnippetBySlot(slot)

    override fun observeSnippetBySlot(slot: Int): Flow<Snippet?> = snippetDao.observeSnippetBySlot(slot)

    override suspend fun getSnippetById(id: Long): Snippet? = snippetDao.getSnippetById(id)

    override suspend fun saveSnippet(snippet: Snippet): Long {
        return if (snippet.id == 0L) {
            snippetDao.insertSnippet(snippet.copy(updatedAt = System.currentTimeMillis()))
        } else {
            snippetDao.updateSnippet(snippet.copy(updatedAt = System.currentTimeMillis()))
            snippet.id
        }
    }

    override suspend fun deleteSnippet(snippet: Snippet) {
        snippetDao.deleteSnippet(snippet)
    }

    override suspend fun togglePin(snippet: Snippet) {
        snippetDao.updateSnippet(
            snippet.copy(
                isPinned = !snippet.isPinned,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun assignQuickSlot(snippetId: Long, slot: Int) {
        if (slot in 1..3) {
            snippetDao.clearSlot(slot)
            snippetDao.assignSlot(snippetId, slot)
        }
    }

    override suspend fun clearQuickSlot(slot: Int) {
        snippetDao.clearSlot(slot)
    }
}
