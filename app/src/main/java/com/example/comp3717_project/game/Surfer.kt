package com.example.comp3717_project.game

import android.graphics.Rect
import com.example.comp3717_project.game.Obstacle.Companion.OBSTACLE_WIDTH

class Surfer(var x: Int, var y: Int, var speed: Int, val gravity: Int) {

    companion object {
        const val JUMP_SPEED = -30

        const val SURFER_WIDTH = 300
        const val SURFER_HEIGHT = 300
        const val SURFER_START_X = 100
        const val SURFER_START_Y = 100
        const val GRAVITY = 2
        // Ratios to determine the surfer's hitbox
        const val SURFER_LEFT_RATIO = 0.2
        const val SURFER_RIGHT_RATIO = 0.306
        const val SURFER_BOTTOM_RATIO = 0.108
    }

    fun update() {
        // Update the surfer's position based on its speed and gravity
        speed += gravity
        y += speed

        // Prevent the surfer from going above the screen
        if (y < 0) {
            y = 0
            speed = 0
        }
    }

    fun jump() {
        // Make the surfer jump by reducing its speed
        speed = JUMP_SPEED
    }


    fun collidesWith(obstacle: Obstacle, game: GameView): Boolean {
        // Check if the surfer collides with an obstacle
        val left = x + (SURFER_WIDTH * SURFER_LEFT_RATIO)
        val top = y + 5
        val right = x + SURFER_WIDTH - (SURFER_WIDTH * SURFER_RIGHT_RATIO)
        val bottom = y + SURFER_HEIGHT - (SURFER_HEIGHT * SURFER_BOTTOM_RATIO)

        val surferRect = Rect(left.toInt(), top, right.toInt(), bottom.toInt())
        val topRect = Rect(obstacle.x, 0, obstacle.x + OBSTACLE_WIDTH, obstacle.topHeight)
        val bottomRect = Rect(obstacle.x, obstacle.bottomY, obstacle.x + OBSTACLE_WIDTH, game.height)

        return surferRect.intersect(topRect) || surferRect.intersect(bottomRect)
    }

}