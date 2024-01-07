package com.example.comp3717_project.game
import kotlin.random.Random

class Cloud(var x: Int, val game: GameView) {

    companion object {
        const val CLOUD_SPEED = 3
        const val CLOUD_WIDTH = 400
        const val CLOUD_HEIGHT = 250
        const val CLOUD_DISTANCE = 600
    }


    private val scale = 1f + Random.nextFloat() * 3f
    val y = Random.nextInt(0, game.height / 2 - CLOUD_HEIGHT)
    val width = (CLOUD_WIDTH * scale).toInt()
    val height = (CLOUD_HEIGHT * scale).toInt()
    private val speed = (CLOUD_SPEED * scale).toInt()

    init {
        println("Cloud: $x, $y, $width, $height, $speed")
    }

    fun update() {
        // Update the cloud's position
        x -= speed
    }
}
