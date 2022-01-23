package main.kotlin.fk_ms_yearly_project.controller

import main.kotlin.fk_ms_yearly_project.model.DifficultyType
import main.kotlin.fk_ms_yearly_project.model.Level
import java.sql.Connection

interface DatabaseManager {
    val db: Connection
    fun getHerokuDB(): String
    fun addLevel(level: Level)
    fun updateLevel(id: Int, level: Level)
    fun deleteLevel(id: Int)
    fun getLevel(id: Int): Level?
    fun getAllLevels(limit: Int = 0, difficulty: DifficultyType = DifficultyType.medium): Array<Level>
}