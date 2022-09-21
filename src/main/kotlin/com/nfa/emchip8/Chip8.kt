package com.nfa.emchip8

import java.io.File

class Chip8(private val screen: Screen) {

    private val mem = IntArray(4096)
    private val stack = IntArray(16)

    private var pc = 0
    private var sp = 0

    private var opcode = 0
    private val V = IntArray(16) // variable registers
    private var I = 0 // index register


    private val chip8_fontset = intArrayOf(
        0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
        0x20, 0x60, 0x20, 0x20, 0x70, // 1
        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
        0xF0, 0x10, 0x20, 0x40, 0x40, // 7
        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
        0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    )

    private val ROM_START = 0x200 // 512
    private val FONT_START = 0x050

    init {
        reset()
    }

    private fun reset() {
        // clear memory
        for (i in mem.indices) {
            mem[i] = 0x0
        }

        opcode = 0
        pc = 0x200
        sp = 0

        load_fontset()
        load_rom("tetris.ch8")
    }

    private fun load_fontset() {
        for (i in chip8_fontset.indices) {
            mem[FONT_START+i] = chip8_fontset[i]
        }
    }

    private fun load_rom(path: String) {

        val file = File(path)
        val buffer = file.readBytes()

        val rom = IntArray(buffer.size)

        for (i in buffer.indices) {
            rom[i] = buffer[i].toUByte().toInt()
        }

        for (i in rom.indices) {
            mem[ROM_START+i] = rom[i]
        }
    }

    fun step() {
        // récupérer l'opcode
        opcode = mem[pc] shl 8 or mem[pc+1]
        println(Integer.toHexString(opcode))
        pc += 2

        val nnn = opcode and 0x0FFF
        val kk = opcode and 0x00FF
        val x = (opcode and 0x0F00) shr 8
        val y = (opcode and 0x00F0) shr 4
        val n = opcode and 0x000F

        // décoder et exécuter l'opcode
        when (opcode) {
            0x00E0 -> { // Clear display
                screen.clear()
                return
            }
        }

        when (opcode and 0xF000) {
            0x1000 -> { // jumps to address NNN
                pc = nnn
                return
            }

            0x2000 -> { // calls subroutine at memory location NNN
                stack[sp++] = pc
                pc = nnn
                return
            }

            0x3000 -> { // skip next instruction if Vx = kk
                if (V[x] == kk) {pc += 2}
                return
            }

            0x4000 -> { // skip next instruction if Vx != kk
                if (V[x] != kk) {pc += 2}
                return
            }

            0x5000 -> { // skip next instruction if Vx = Vy
                if (V[x] == V[y]) {pc += 2}
                return
            }

            0x6000 -> { // set Vx = kk
                V[x] = kk
                return
            }

            0x7000 -> { // add value to register Vx
                V[x] += kk
                if (V[x] >= 256) {
                    V[x] -= 256
                }
                return
            }

            0x9000 -> { // skip next instruction if Vx != Vy
                if (V[x] != V[y]) {pc += 2}
                return
            }

            0xA000 -> { // set index register
                I = nnn
                return
            }


            0xD000 -> { // display

                val xpos = V[x] % 64
                val ypos = V[y] % 32

                V[0xF] = 0

                var rowData: Int
                var screenPixel: Boolean
                var spritePixel: Boolean
                var xcoord: Int
                var ycoord: Int

                for (row in 0 until n) {
                    rowData = mem[row+I]
                    for (bit in 0 until 8) {

                        xcoord = xpos + bit
                        ycoord = ypos + row

                        if (xcoord >= screen.getCanvasWidth() || ycoord >= screen.getCanvasHeight()) {
                            continue
                        }

                        screenPixel = screen.getPixel(xcoord, ycoord)
                        spritePixel = rowData and (1 shl 7 - bit) != 0

                        if (screenPixel && spritePixel) {
                            V[0xF] = 1
                            screen.setPixel(xcoord, ycoord, false)
                        }

                        if (!screenPixel && spritePixel) {
                            screen.setPixel(xcoord, ycoord, true)
                        }
                    }
                }
            }
        }
        when (opcode and 0xF00F) {
            0x8000 -> { // set Vx = Vy
                V[x] = V[y]
                return
            }

            0x8001 -> { // set Vx = (Vx OR Vy)
                V[x] = V[x] or V[y]
                return
            }

            0x8002 -> { // set Vx = (Vx AND Vy)
                V[x] = V[x] and V[y]
                return
            }

            0x8003 -> { // set Vx = (Vx XOR Vy)
                V[x] = V[x] xor V[y]
                return
            }

            0x8004 -> { // add Vy to Vx
                V[x] += V[y]
                if (V[x] >= 256) {
                    V[x] -= 256
                    V[0xF] = 1
                } else {
                    V[0xF] = 0
                }
                return
            }
            
            0x8005 -> { // substract Vy from Vx
                V[x] -= V[y]
                if (V[x] < 0) {
                    V[x] += 256
                    V[0xF] = 0
                } else {
                    V[0xF] = 1
                }
                return
            }

            0x8006 -> { // set Vx = Vx SHR 1
                V[0xF] = if ((V[x] and 0x1) == 1) 1 else 0
                V[x] = V[x] shr 1
                return
            }

            0x800E -> {
                V[0xF] = if ((V[x] shr 7) == 1) 1 else 0
                V[x] = (V[x] shl 1) and 0xFF
                return
            }

            0x8007 -> { // set Vx = Vy - Vx
                V[x] = V[y] - V[x]
                if (V[x] < 0) {
                    V[x] += 256
                    V[0xF] = 0
                } else {
                    V[0xF] = 1
                }
                return
            }
        }
    }
}