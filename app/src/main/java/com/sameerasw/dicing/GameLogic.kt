package com.sameerasw.dicing

import com.sameerasw.dicing.DiceLogic.rerollDice
import kotlin.random.Random

object GameLogic {
    /**
     * Computer Player Strategy for Dice Rerolling
     *
     * Strategy 1 (Smart): Reroll dice with values below 3
     * The computer uses a strategy to maximize its score by rerolling
     * dice with low values (below 3). This is based on the fact that
     * the average value of a die is 3.5, so rerolling dice with values below 3
     * has a higher chance of improving the score.
     *
     * Strategy 2 (Random): Randomly decide which dice to reroll
     * The computer randomly selects dice to reroll without any specific strategy.
     * This makes the game easier(allegedly... ) for the player as the computer
     *
     * @param diceValues Current dice values of the computer
     * @param rerollCount Current reroll count up to 2
     * @param useSmartStrategy Whether to use the smart strategy
     * @return Pair New dice values and the reroll count
     */
    fun computerReroll(
        diceValues: List<Int>,
        rerollCount: Int,
        useSmartStrategy: Boolean = true
    ): Pair<List<Int>, Int> {
        // Check for max reroll count
        if (rerollCount >= 2) {
            return Pair(diceValues, rerollCount)
        }

        // Pick which dice to reroll
        val diceToReroll = MutableList(5) { false }
        var shouldReroll = false

        if (useSmartStrategy) {
            // Smart Strategy: Mark dice with values below 3 for rerolling
            diceValues.forEachIndexed { index, value ->
                if (value < 3) {
                    diceToReroll[index] = true
                    shouldReroll = true
                }
            }
        } else {
            // Random Strategy: Randomly decide which dice to reroll (easier mode)
            diceValues.forEachIndexed { index, _ ->
                if (Random.nextBoolean()) {
                    diceToReroll[index] = true
                    shouldReroll = true
                }
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