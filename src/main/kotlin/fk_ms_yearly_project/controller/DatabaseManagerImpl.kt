package main.kotlin.fk_ms_yearly_project.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import main.kotlin.fk_ms_yearly_project.model.DifficultyType
import main.kotlin.fk_ms_yearly_project.model.Level
import main.kotlin.fk_ms_yearly_project.model.LevelItem
import java.lang.reflect.Type
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

val levelItemType: Type = object : TypeToken<Array<LevelItem>>() {}.type

object DatabaseManagerImpl : DatabaseManager {
    override val db: Connection = DriverManager.getConnection(getHerokuDB())

    override fun getHerokuDB(): String {
        val processBuilder = ProcessBuilder()

        return processBuilder.environment()["JDBC_DATABASE_URL"]
            ?: "URL GOES HERE"
    }

    override fun addLevel(level: Level) {
        val stmt = db.createStatement()
        stmt.executeUpdate("INSERT INTO levels(difficulty, data) VALUES('${level.difficulty}', '${Gson().toJson(level.data)}')")
    }

    override fun updateLevel(id: Int, level: Level) {
        val stmt = db.createStatement()
        stmt.executeUpdate("UPDATE levels set difficulty='${level.difficulty}', data='${Gson().toJson(level.data)}' WHERE id=${id}")
    }

    override fun deleteLevel(id: Int) {
        val stmt = db.createStatement()
        stmt.executeUpdate("DELETE FROM levels WHERE id=$id")
    }

    override fun getLevel(id: Int): Level? {
        val stmt = db.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM levels WHERE id=$id")

        rs.next()

        return if(rs.isFirst) {
            val levelData = Gson().fromJson<Array<LevelItem>>(rs.getString("data"), levelItemType)

            Level(rs.getInt("id"), DifficultyType.valueOf(rs.getString("difficulty")), levelData)
        } else {
            null
        }
    }

    override fun getAllLevels(limit: Int, difficulty: DifficultyType): Array<Level> {
        val stmt = db.createStatement()

        val rs: ResultSet = if (limit == 0) {
            stmt.executeQuery("SELECT * FROM levels ORDER BY id")
        } else {
            stmt.executeQuery("SELECT * FROM levels WHERE difficulty='${difficulty}' ORDER BY random() LIMIT $limit")
        }

        val levels: MutableList<Level> = mutableListOf()

        while (rs.next()) {
            val levelData = Gson().fromJson<Array<LevelItem>>(rs.getString("data"), levelItemType)

            val level = Level(rs.getInt("id"), DifficultyType.valueOf(rs.getString("difficulty")), levelData)
            levels.add(level)
        }

        return levels.toTypedArray()
    }
}