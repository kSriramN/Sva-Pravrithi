package com.svapravrithi.app.data.backup

import com.svapravrithi.app.data.local.entity.DeclarationEntity
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.data.local.entity.ScoringConfigEntity
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PlanPriority
import org.json.JSONArray
import org.json.JSONObject

const val BACKUP_FILE_NAME = "sva_pravrithi_backup.json"
private const val BACKUP_SCHEMA_VERSION = 1

data class BackupSnapshot(
    val exportedAtMillis: Long,
    val expenses: List<ExpenseEntity>,
    val plans: List<PlanEntity>,
    val declarations: List<DeclarationEntity>,
    val scoringConfig: ScoringConfigEntity?,
)

/**
 * Plain org.json (Android)-based mapper \u2014 avoids pulling in a serialization library
 * for what is a small, stable set of fields. All fields are primitives/enums-as-strings,
 * so this is straightforward to keep in sync if entities change.
 */
object BackupJsonMapper {

    fun toJson(snapshot: BackupSnapshot): String {
        val root = JSONObject()
        root.put("schemaVersion", BACKUP_SCHEMA_VERSION)
        root.put("exportedAtMillis", snapshot.exportedAtMillis)

        root.put("expenses", JSONArray().apply {
            snapshot.expenses.forEach { e ->
                put(
                    JSONObject().apply {
                        put("id", e.id)
                        put("amount", e.amount)
                        put("category", e.category)
                        put("type", e.type.name)
                        put("guna", e.guna?.name ?: JSONObject.NULL)
                        put("comments", e.comments)
                        put("date", e.date)
                        put("yearMonth", e.yearMonth)
                    },
                )
            }
        })

        root.put("plans", JSONArray().apply {
            snapshot.plans.forEach { p ->
                put(
                    JSONObject().apply {
                        put("id", p.id)
                        put("title", p.title)
                        put("estimatedAmount", p.estimatedAmount)
                        put("dueDate", p.dueDate)
                        put("type", p.type.name)
                        put("guna", p.guna.name)
                        put("priority", p.priority.name)
                        put("notes", p.notes)
                        put("isCompleted", p.isCompleted)
                        put("yearMonth", p.yearMonth)
                    },
                )
            }
        })

        root.put("declarations", JSONArray().apply {
            snapshot.declarations.forEach { d ->
                put(
                    JSONObject().apply {
                        put("yearMonth", d.yearMonth)
                        put("savingsGoal", d.savingsGoal)
                        put("needsBudget", d.needsBudget)
                        put("wantsBudget", d.wantsBudget)
                        put("pleasuresBudget", d.pleasuresBudget)
                        put("actualSavings", d.actualSavings)
                    },
                )
            }
        })

        snapshot.scoringConfig?.let { c ->
            root.put(
                "scoringConfig",
                JSONObject().apply {
                    put("baseScore", c.baseScore)
                    put("wantsDeductionDivisor", c.wantsDeductionDivisor)
                    put("pleasureDeductionDivisor", c.pleasureDeductionDivisor)
                    put("savingsBonusMultiplier", c.savingsBonusMultiplier)
                    put("savingsPenaltyMultiplier", c.savingsPenaltyMultiplier)
                },
            )
        }

        return root.toString(2)
    }

    fun fromJson(jsonText: String): BackupSnapshot {
        val root = JSONObject(jsonText)

        val expenses = root.optJSONArray("expenses")?.let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                ExpenseEntity(
                    id = o.optLong("id", 0),
                    amount = o.getDouble("amount"),
                    category = o.getString("category"),
                    type = ExpenseType.valueOf(o.getString("type")),
                    guna = o.optString("guna", "").takeIf { it.isNotBlank() }?.let { Guna.valueOf(it) },
                    comments = o.optString("comments", ""),
                    date = o.getLong("date"),
                    yearMonth = o.getString("yearMonth"),
                )
            }
        } ?: emptyList()

        val plans = root.optJSONArray("plans")?.let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                PlanEntity(
                    id = o.optLong("id", 0),
                    title = o.getString("title"),
                    estimatedAmount = o.getDouble("estimatedAmount"),
                    dueDate = o.getLong("dueDate"),
                    type = ExpenseType.valueOf(o.getString("type")),
                    guna = Guna.valueOf(o.getString("guna")),
                    priority = PlanPriority.valueOf(o.getString("priority")),
                    notes = o.optString("notes", ""),
                    isCompleted = o.optBoolean("isCompleted", false),
                    yearMonth = o.getString("yearMonth"),
                )
            }
        } ?: emptyList()

        val declarations = root.optJSONArray("declarations")?.let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                DeclarationEntity(
                    yearMonth = o.getString("yearMonth"),
                    savingsGoal = o.getDouble("savingsGoal"),
                    needsBudget = o.getDouble("needsBudget"),
                    wantsBudget = o.getDouble("wantsBudget"),
                    pleasuresBudget = o.getDouble("pleasuresBudget"),
                    actualSavings = o.optDouble("actualSavings", 0.0),
                )
            }
        } ?: emptyList()

        val scoringConfig = root.optJSONObject("scoringConfig")?.let { o ->
            ScoringConfigEntity(
                baseScore = o.optDouble("baseScore", 50.0),
                wantsDeductionDivisor = o.optDouble("wantsDeductionDivisor", 2.0),
                pleasureDeductionDivisor = o.optDouble("pleasureDeductionDivisor", 2.0),
                savingsBonusMultiplier = o.optDouble("savingsBonusMultiplier", 1.0),
                savingsPenaltyMultiplier = o.optDouble("savingsPenaltyMultiplier", 1.0),
            )
        }

        return BackupSnapshot(
            exportedAtMillis = root.optLong("exportedAtMillis", System.currentTimeMillis()),
            expenses = expenses,
            plans = plans,
            declarations = declarations,
            scoringConfig = scoringConfig,
        )
    }
}
