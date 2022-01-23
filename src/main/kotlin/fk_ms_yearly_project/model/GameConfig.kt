package fk_ms_yearly_project.model

data class GameConfig (
    val easyCount: Int,
    val mediumCount: Int,
    val hardCount: Int,
    val totalScore: Int,
    val levelCount: Int = easyCount + mediumCount + hardCount
)