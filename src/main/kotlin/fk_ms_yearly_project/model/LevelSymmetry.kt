package main.kotlin.fk_ms_yearly_project.model

object LevelSymmetry {
    private fun verticalFlip(level: Level): Level {
        val newData = mutableListOf<LevelItem>()

        level.data.forEach {
            val x = level.sizeX - 1 - it.x
            val z = it.z
            newData.add(LevelItem(10*x + z, x, z, it.type))
        }

        return Level(level.id, level.difficulty, newData.toTypedArray())
    }

    private fun horizontalFlip(level: Level): Level {
        val newData = mutableListOf<LevelItem>()

        level.data.forEach {
            val x = it.x
            val z = level.sizeZ - 1 - it.z
            newData.add(LevelItem(10*x + z, x, z, it.type))
        }

        return Level(level.id, level.difficulty, newData.toTypedArray())
    }

    private fun rotateClockwise(level: Level): Level {
        val newData = mutableListOf<LevelItem>()

        level.data.forEach {
            val x = level.sizeZ - 1 - it.z
            val z = it.x
            newData.add(LevelItem(10*x + z, x, z, it.type))
        }

        return Level(level.id, level.difficulty, newData.toTypedArray())
    }

    private fun rotateCounterClockwise(level: Level): Level {
        val newData = mutableListOf<LevelItem>()

        level.data.forEach {
            val x = it.z
            val z = level.sizeX - 1 - it.x
            newData.add(LevelItem(10*x + z, x, z, it.type))
        }

        return Level(level.id, level.difficulty, newData.toTypedArray())
    }

    fun randomSymmetry(level: Level): Level {
        val random = (0 until 8).random()
        println("SYMMETRY: $random")

        return when(random) {
            1 -> {
                rotateClockwise(level)
            }
            2 -> {
                verticalFlip(horizontalFlip(level))
            }
            3 -> {
                rotateCounterClockwise(level)
            }
            4 -> {
                verticalFlip(level)
            }
            5 -> {
                horizontalFlip(rotateClockwise(level))
            }
            6 -> {
                horizontalFlip(level)
            }
            7 -> {
                horizontalFlip(rotateCounterClockwise(level))
            }
            else -> level
        }
    }
}