package com.svapravrithi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PlanPriority

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val estimatedAmount: Double,
    val dueDate: Long,
    val type: ExpenseType,
    val guna: Guna,
    val priority: PlanPriority,
    val notes: String = "",
    val isCompleted: Boolean = false,
    val yearMonth: String,
)
