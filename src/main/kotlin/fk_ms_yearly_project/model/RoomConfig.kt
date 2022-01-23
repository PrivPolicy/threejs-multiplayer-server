package main.kotlin.fk_ms_yearly_project.model

class RoomConfig(
    val easyCount: Int = 2,
    val mediumCount: Int = 3,
    val hardCount: Int = 2,
    private val scorePerEasy: Int = 5,
    private val scorePerMedium: Int = 11,
    private val scorePerHard: Int = 13
) {
    val levelCount = easyCount + mediumCount + hardCount
    val scoreTotal = easyCount * scorePerEasy + mediumCount * scorePerMedium + hardCount * scorePerHard

    fun getLevelScore(level: Level): Int {
        return when (level.difficulty) {
            DifficultyType.easy -> scorePerEasy
            DifficultyType.medium -> scorePerMedium
            DifficultyType.hard -> scorePerHard
        }
    }
}