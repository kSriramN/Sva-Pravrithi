package com.svapravrithi.app.domain.model

import androidx.compose.ui.graphics.Color
import com.svapravrithi.app.ui.theme.Rajasik
import com.svapravrithi.app.ui.theme.Satvik
import com.svapravrithi.app.ui.theme.Tamasik

/** The Three Gunas — quality of a spend, chosen manually on every expense/plan entry. */
enum class Guna(val label: String, val color: Color) {
    SATVIK("Satvik", Satvik),
    RAJASIK("Rajasik", Rajasik),
    TAMASIK("Tamasik", Tamasik),
}

/** Need / Want / Pleasure classification, independent of Guna. */
enum class ExpenseType(val label: String) {
    NEED("Need"),
    WANT("Want"),
    PLEASURE("Pleasure"),
}

enum class PlanPriority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
}
