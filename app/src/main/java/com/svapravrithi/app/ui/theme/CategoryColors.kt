package com.svapravrithi.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Since categories are user-editable (not a fixed enum), colors can't be hardcoded per
 * category. This assigns a color by hashing the category name against a fixed palette,
 * so a given category always renders the same color across screens and app restarts,
 * without needing to persist a color choice anywhere.
 */
private val CATEGORY_PALETTE = listOf(
    Color(0xFF2E7D32), // Satvik green
    Color(0xFFF9A825), // Rajasik amber
    Color(0xFFC62828), // Tamasik red
    Color(0xFF1E88E5), // blue
    Color(0xFF8E24AA), // purple
    Color(0xFF00897B), // teal
    Color(0xFFD81B60), // pink
    Color(0xFF6D4C41), // brown
    Color(0xFF546E7A), // blue grey
    Color(0xFFFB8C00), // orange
    Color(0xFF3949AB), // indigo
    Color(0xFF7CB342), // light green
)

fun colorForCategory(category: String): Color =
    CATEGORY_PALETTE[Math.floorMod(category.hashCode(), CATEGORY_PALETTE.size)]