package main.kotlin.fk_ms_yearly_project.model

data class Level(
    val id: Int = 0,
    val difficulty: DifficultyType = DifficultyType.medium,
    val data: Array<LevelItem> = arrayOf(),
) {
    val sizeX: Int
        get() = data.fold(0) { max, current -> max.coerceAtLeast(current.x) } + 1

    val sizeZ: Int
        get() = data.fold(0) { max, current -> max.coerceAtLeast(current.z) } + 1

    val minX: Int
        get() = data.fold(0) { min, current -> min.coerceAtMost(current.x) }
    val maxX: Int
        get() = data.fold(0) { max, current -> max.coerceAtLeast(current.x) }
    val minZ: Int
        get() = data.fold(0) { min, current -> min.coerceAtMost(current.z) }
    val maxZ: Int
        get() = data.fold(0) { max, current -> max.coerceAtLeast(current.z) }
}
