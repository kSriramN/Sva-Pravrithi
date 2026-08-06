package com.svapravrithi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PaymentMethod

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val type: ExpenseType,
    /** Optional — for the user's own reflection only. Does not drive the Dominant Guna calculation. */
    val guna: Guna? = null,
    /** Optional — how this was paid (Cash/Card/Account), for the user's own record-keeping only. */
    val paymentMethod: PaymentMethod? = null,
    val comments: String = "",
    /** epoch millis, start of day */
    val date: Long,
    /** yyyyMM, denormalized for fast monthly queries/reflection */
    val yearMonth: String,
)
