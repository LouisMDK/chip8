package com.nfa.emchip8

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage

class HelloApplication : Application() {
    override fun start(stage: Stage) {


        val root = Group()
        val scene = Scene(root, 800.0, 550.0)

        val screen = Screen()
        root.children.add(screen)

        stage.title = "Chip8"
        stage.scene = scene
        stage.show()

        val chip8 = Chip8(screen)

        for (i in 0..1000) {
            chip8.step()
        }
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}