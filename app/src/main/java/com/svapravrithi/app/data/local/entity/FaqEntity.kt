package com.svapravrithi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faqs")
data class FaqEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val answer: String,
    val createdAt: Long = System.currentTimeMillis(),
)
