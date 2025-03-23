package com.sameerasw.dicing

import kotlin.random.Random

object GameLogic {

    fun rerollDice(diceValues: List<Int>, selectedDice: List<Boolean>): List<Int> {
        return diceValues.mapIndexed { index, value ->
            if (selectedDice[index]) Random.nextInt(1, 7) else value
        }
    }

    fun rollDice(): List<Int> {
        return List(5) { Random.nextInt(1, 7) }
    }

    //Computer Logic to re-roll selected dices or not
    fun computerReroll(diceValues: List<Int>, rerollCount: Int): Pair<List<Int>, Int> {
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