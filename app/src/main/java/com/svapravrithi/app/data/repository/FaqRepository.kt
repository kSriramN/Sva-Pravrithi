package com.svapravrithi.app.data.repository

import com.svapravrithi.app.data.local.dao.FaqDao
import com.svapravrithi.app.data.local.entity.FaqEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

private val DEFAULT_FAQS = listOf(
    "How is my monthly Guna determined?" to
        "It's based on how closely you stick to the Savings Goal and Needs/Wants/Pleasures budgets you declare each month \u2014 not on individual purchases. Meeting your savings goal and staying within budget leans Sattvik; overspending on Wants leans Rajasik; missing your savings goal and overspending on Pleasures leans Tamasik.",
    "Why do I need to update my savings separately?" to
        "Your Savings Goal is the target you declare at the start of the month. Actual Savings is the real amount you've put aside, which you record yourself under Profile > Update Savings whenever you actually set money aside.",
    "Is my data backed up anywhere?" to
        "Everything is stored locally on your device by default. You can optionally back up to your own Google Drive from Profile > Backup & Restore.",
)

@Singleton
class FaqRepository @Inject constructor(private val dao: FaqDao) {

    fun observeAll(): Flow<List<FaqEntity>> = dao.observeAll()

    suspend fun add(question: String, answer: String) {
        if (question.isBlank() || answer.isBlank()) return
        dao.insert(FaqEntity(question = question.trim(), answer = answer.trim()))
    }

    suspend fun delete(faq: FaqEntity) = dao.delete(faq)

    suspend fun ensureSeeded() {
        if (dao.count() == 0) {
            dao.insertAll(DEFAULT_FAQS.map { (q, a) -> FaqEntity(question = q, answer = a) })
        }
    }
}
