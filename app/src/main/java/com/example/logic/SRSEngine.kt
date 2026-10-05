package com.example.logic

import kotlin.math.max
import kotlin.math.roundToInt

data class SRSResult(
    val intervalDays: Int,
    val easeFactor: Float,
    val repetitions: Int,
    val nextReviewDateMillis: Long
)

object SRSEngine {
    private const val ONE_DAY_MILLIS = 24L * 60 * 60 * 1000

    /**
     * SuperMemo-2 (SM-2) Spaced Repetition calculation
     * @param quality recall score from 0 (complete blackout) to 5 (perfect recall)
     * @param previousInterval previous interval in days
     * @param previousEaseFactor ease factor (minimum 1.3)
     * @param repetitions previous consecutive successful repetitions
     */
    fun calculateNextReview(
        quality: Int,
        previousInterval: Int,
        previousEaseFactor: Float,
        repetitions: Int,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SRSResult {
        val q = quality.coerceIn(0, 5)

        // Calculate new Ease Factor: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val delta = 0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f)
        val newEaseFactor = max(1.3f, previousEaseFactor + delta)

        val newRepetitions: Int
        val newInterval: Int

        if (q < 3) {
            // Failed recall: reset repetitions and schedule for 1 day
            newRepetitions = 0
            newInterval = 1
        } else {
            newRepetitions = repetitions + 1
            newInterval = when (newRepetitions) {
                1 -> 1
                2 -> 6
                else -> (previousInterval * newEaseFactor).roundToInt().coerceAtLeast(1)
            }
        }

        val nextReviewDate = currentTimeMillis + (newInterval.toLong() * ONE_DAY_MILLIS)

        return SRSResult(
            intervalDays = newInterval,
            easeFactor = newEaseFactor,
            repetitions = newRepetitions,
            nextReviewDateMillis = nextReviewDate
        )
    }
}
