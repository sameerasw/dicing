package com.sameerasw.dicing

import com.sameerasw.dicing.DiceLogic.rerollDice

object GameLogic {
    /**
     * Computer Player Strategy for Dice Rerolling
     *
     * The computer uses a simple strategy to maximize its score by rerolling
     * dice with low values (below 3). This strategy is based on the fact to
     * try to keep the roll above the average count which is 3.5. So the computer
     * has a better chance of improving its score by rerolling dice with values
     *
     * algorithm:
     * 1. Check if maximum reroll count (2) has been reached
     * 2. Identify all dice with low values
     * 3. Reroll these low-value dices
     *
     * Advantages:
     * - Simple and easy to understand
     * - Efficient
     * - High probability of improving score
     * - Easy to implement
     *
     * @param diceValues Current dice values of the computer
     * @param rerollCount Current reroll countup to 2
     * @return Pair New dice values and the reroll count
     */
    fun computerReroll(
        diceValues: List<Int>,
        rerollCount: Int,
    ): Pair<List<Int>, Int> {
        // Check for max reroll count
        if (rerollCount >= 2) {
            return Pair(diceValues, rerollCount)
        }

        // Pick which dice to reroll
        val diceToReroll = MutableList(5) { false }
        var shouldReroll = false

        // Mark dice with values below 3 for rerolling
        diceValues.forEachIndexed { index, value ->
            if (value < 3) {
                diceToReroll[index] = true
                shouldReroll = true
            }
        }

        // If there are dice to reroll, perform the reroll and increment the count
        return if (shouldReroll) {
            Pair(rerollDice(diceValues, diceToReroll), rerollCount + 1)
        } else {
            // No dice to reroll, return original values and count
            Pair(diceValues, rerollCount)
        }
    }
}