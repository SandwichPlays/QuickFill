package com.byteutility.dev.quickfill.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {

    @Query("SELECT * FROM snippets ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllSnippets(): Flow<List<Snippet>>

    @Query("SELECT * FROM snippets WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY isPinned DESC, updatedAt DESC")
    fun searchSnippets(query: String): Flow<List<Snippet>>

    @Query("SELECT * FROM snippets WHERE quickSlot = :slot LIMIT 1")
    suspend fun getSnippetBySlot(slot: Int): Snippet?

    @Query("SELECT * FROM snippets WHERE quickSlot = :slot LIMIT 1")
    fun observeSnippetBySlot(slot: Int): Flow<Snippet?>

    @Query("SELECT * FROM snippets WHERE id = :id LIMIT 1")
    suspend fun getSnippetById(id: Long): Snippet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: Snippet): Long

    @Update
    suspend fun updateSnippet(snippet: Snippet)

    @Delete
    suspend fun deleteSnippet(snippet: Snippet)

    @Query("UPDATE snippets SET quickSlot = 0 WHERE quickSlot = :slot")
    suspend fun clearSlot(slot: Int)

    @Query("UPDATE snippets SET quickSlot = :slot WHERE id = :snippetId")
    suspend fun assignSlot(snippetId: Long, slot: Int)
}
