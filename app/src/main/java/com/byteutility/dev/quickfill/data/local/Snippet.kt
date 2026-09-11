package com.byteutility.dev.quickfill.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snippets")
data class Snippet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val quickSlot: Int = 0, // 0 = not in quick panel, 1 = Slot 1, 2 = Slot 2, 3 = Slot 3
    val iconName: String = "clipboard", // clipboard, email, phone, link, code, text, star
    val colorIndex: Int = 0,
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
