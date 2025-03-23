package com.sameerasw.dicing

import kotlin.random.Random

object DiceLogic {
    fun rerollDice(diceValues: List<Int>, selectedDice: List<Boolean>): List<Int> {
        return diceValues.mapIndexed { index, value ->
            if (selectedDice[index]) value else Random.nextInt(1, 7)
        }
    }

    fun rollDice(): List<Int> {
        return List(5) { Random.nextInt(1, 7) }
    }
}