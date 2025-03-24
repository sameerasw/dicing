package com.sameerasw.dicing

import com.sameerasw.dicing.DiceLogic.rerollDice
import kotlin.random.Random

object GameLogic {
    fun computerReroll(diceValues: List<Int>, rerollCount: Int): Pair<List<Int>, Int> {
        // Computer rerolls if it has not rerolled twice and randomly decides to reroll
        var newDiceValues = diceValues
        var newRerollCount = rerollCount

        // Randomly decide whether to reroll
        if (rerollCount < 2 && Random.nextBoolean()) {
            val diceToKeep = List(5) { Random.nextBoolean() }
            newDiceValues = rerollDice(diceValues, diceToKeep.map { !it }) // Invert for reroll
            newRerollCount++
        }

        return Pair(newDiceValues, newRerollCount)
    }
}