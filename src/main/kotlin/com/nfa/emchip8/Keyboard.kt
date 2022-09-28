package com.nfa.emchip8

import javafx.scene.input.KeyCode

class Keyboard {

    private val keys = BooleanArray(16)
    private val keyboardMap: HashMap<KeyCode, Int> = hashMapOf(
        KeyCode.DIGIT1 to 0, KeyCode.DIGIT2 to 1, KeyCode.DIGIT3 to 2, KeyCode.DIGIT4 to 3,
        KeyCode.A to 4, KeyCode.Z to 5, KeyCode.E to 6, KeyCode.R to 7,
        KeyCode.Q to 8, KeyCode.S to 9, KeyCode.D to 10, KeyCode.F to 11,
        KeyCode.W to 12, KeyCode.X to 13, KeyCode.C to 14, KeyCode.V to 15
    )

    init {
        clear()
    }

    fun clear() {
        for (i in keys.indices) {
            keys[i] = false
        }
    }

    fun setKeyState(key: KeyCode, pressed: Boolean): Boolean {
        if (!keyboardMap.containsKey(key)) {
            return false
        }
        keys[keyboardMap[key]!!] = true
        return true
    }

    fun isPressed(keyInt: Int): Boolean {
        require(keyInt in 0..15)
        return keys[keyInt]
    }

}