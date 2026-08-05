package com.svapravrithi.app.data.local

import androidx.room.TypeConverter
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PlanPriority

class Converters {
    @TypeConverter
    fun fromGuna(value: Guna): String = value.name

    @TypeConverter
    fun toGuna(value: String): Guna = Guna.valueOf(value)

    @TypeConverter
    fun fromExpenseType(value: ExpenseType): String = value.name

    @TypeConverter
    fun toExpenseType(value: String): ExpenseType = ExpenseType.valueOf(value)

    @TypeConverter
    fun fromPriority(value: PlanPriority): String = value.name

    @TypeConverter
    fun toPriority(value: String): PlanPriority = PlanPriority.valueOf(value)
}
