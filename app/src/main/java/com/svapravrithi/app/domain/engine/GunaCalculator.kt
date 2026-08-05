package com.svapravrithi.app.domain.engine

import com.svapravrithi.app.domain.model.Guna

/** Reused by GunaMandala/DonutChart-style visuals: a normalized % breakdown plus a dominant pick. */
data class GunaDistribution(
    val percentages: Map<Guna, Double>, // sums to ~100.0
    val dominant: Guna,
) {
    fun percentOf(guna: Guna): Int = (percentages[guna] ?: 0.0).let { Math.round(it).toInt() }
}
