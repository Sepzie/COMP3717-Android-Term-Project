package com.example.comp3717_project.game

import kotlin.random.Random


class Obstacle(var x: Int, val game: GameView) {

    companion object {
        const val OBSTACLE_SPEED = 6
        const val OBSTACLE_WIDTH = 250
        const val OBSTACLE_GAP = 700
        const val OBSTACLE_DISTANCE = 600
    }


    val topHeight = Random.nextInt(100, game.height - OBSTACLE_GAP - 100)
    val bottomY = topHeight + OBSTACLE_GAP
    var hasBeenPassed = false

    fun pass() {
        hasBeenPassed = true
    }
    fun update() {
        // Update the obstacle's position
        x -= OBSTACLE_SPEED + game.score
    }

}
