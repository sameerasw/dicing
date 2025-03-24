package com.sameerasw.dicing

import kotlin.random.Random

object DiceLogic {
    fun rerollDice(diceValues: List<Int>, selectedDice: List<Boolean>): List<Int> {
        // Reroll the dice that are selected
        return diceValues.mapIndexed { index, value ->
            if (selectedDice[index]) value else Random.nextInt(1, 7)
        }
    }

    fun rollDice(): List<Int> {
        // Roll 5 dice
        return List(5) { Random.nextInt(1, 7) }
    }
}