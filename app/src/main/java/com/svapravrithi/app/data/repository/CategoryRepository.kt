package com.svapravrithi.app.data.repository

import com.svapravrithi.app.data.local.dao.CategoryDao
import com.svapravrithi.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** The original built-in categories, used only to seed the table on first launch. */
val DEFAULT_CATEGORIES = listOf(
    "Food & Dining", "Groceries", "Rent", "Utilities", "Transport",
    "Shopping", "Entertainment", "Health", "Education", "Travel", "Gifts", "Other",
)

@Singleton
class CategoryRepository @Inject constructor(private val dao: CategoryDao) {

    fun observeAll(): Flow<List<CategoryEntity>> = dao.observeAll()

    suspend fun add(name: String) {
        if (name.isBlank()) return
        dao.insert(CategoryEntity(name = name.trim(), sortOrder = Int.MAX_VALUE))
    }

    suspend fun delete(category: CategoryEntity) = dao.delete(category)

    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(DEFAULT_CATEGORIES.mapIndexed { index, name -> CategoryEntity(name = name, sortOrder = index) })
        }
    }
}
