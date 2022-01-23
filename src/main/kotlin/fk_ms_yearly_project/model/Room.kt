package main.kotlin.fk_ms_yearly_project.model

import com.google.gson.Gson
import fk_ms_yearly_project.model.GameConfig
import org.eclipse.jetty.websocket.api.Session
import main.kotlin.fk_ms_yearly_project.controller.DatabaseManagerImpl
import java.lang.Exception
import kotlinx.coroutines.*
import org.json.JSONObject
import sun.security.ec.point.ProjectivePoint
import kotlin.coroutines.EmptyCoroutineContext

class Room(
    private val user1: Socket,
    private val user2: Socket
) {
    private var roomConfig: RoomConfig
    private val roomLevels: MutableList<Level> = mutableListOf()
    private val user1Levels: MutableList<Level> = mutableListOf()
    private val user2Levels: MutableList<Level> = mutableListOf()
    private var user1CurrentLevel: Level? = null
    private var user2CurrentLevel: Level? = null
    private var user1Score: Int = 0
    private var user2Score: Int = 0

    init {
        user1.room = this
        user2.room = this

        roomConfig = RoomConfigLibrary.getRandom()

        val tempLevels: MutableList<Level> = mutableListOf()
        tempLevels += DatabaseManagerImpl.getAllLevels(roomConfig.easyCount, DifficultyType.easy).toMutableList()
        tempLevels += DatabaseManagerImpl.getAllLevels(roomConfig.mediumCount, DifficultyType.medium).toMutableList()
        tempLevels += DatabaseManagerImpl.getAllLevels(roomConfig.hardCount, DifficultyType.hard).toMutableList()

        tempLevels.forEach {
            val level = LevelSymmetry.randomSymmetry(it)
            roomLevels += level
            println("FOR LEVEL ${level.id}, SIZE ${level.sizeX}x, ${level.sizeZ}z, X ${level.minX}, ${level.maxX}, Z ${level.minZ}, ${level.maxZ}")
        }

        user1Levels += roomLevels.toMutableList()
        user2Levels += roomLevels.toMutableList()

        val config = GameConfig(roomConfig.easyCount, roomConfig.mediumCount, roomConfig.hardCount, roomConfig.scoreTotal)

        user1.sendMessage("config", Gson().toJson(config))
        user2.sendMessage("config", Gson().toJson(config))

        CoroutineDispatcher.dispatch {
            delay(5000)
            randomLevel(user1)
            randomLevel(user2)
            user1.sendMessage("new_level", Gson().toJson(user1CurrentLevel))
            user2.sendMessage("new_level", Gson().toJson(user2CurrentLevel))
        }
    }

    private fun sessionToSocket(user: Session): Socket {
        return if(user == user1.session) {
            user1
        } else {
            user2
        }
    }

    fun onClose(user: Session, statusCode: Int, reason: String?) {
        user1.room = null
        user2.room = null

        emit(user, "forfeit")

        destroy()
    }

    fun onMessage(user: Session, message: String) {
        val data = Gson().fromJson(message, Message::class.java)

        when (data.title) {
            "heartbeat" -> { onHeartbeat(user) }
            "done" -> { onDone(user) }
            "powerup_use" -> { onPowerup(user, data) }
        }
    }

    private fun emit(user: Session, title: String, data: String = "{}") {
        if (user == user1.session) {
            user2.sendMessage(title, data)
        } else if (user == user2.session) {
            user1.sendMessage(title, data)
        }
    }

    private fun emit(user: Session, message: Message) {
        emit(user, message.title, message.data)
    }

    fun broadcast(title: String, data: String = "{}") {
        user1.sendMessage(title, data)
        user2.sendMessage(title, data)
    }

    private fun destroy() {
        user1.room = null
        user2.room = null
        user1.close()
        user2.close()
        SocketManager.rooms.remove(this)
    }

    private fun randomLevel(user: Socket) {
        if (user == user1) {
            if(user1Levels.size == 0) {
                user1.sendMessage("win")
                user2.sendMessage("lose")
                destroy()
            } else {
                val random = (0 until user1Levels.size).random()
                user1CurrentLevel = user1Levels.removeAt(random)
            }
        } else if (user == user2) {
            if(user2Levels.size == 0) {
                user1.sendMessage("lose")
                user2.sendMessage("win")
                destroy()
            } else {
                val random = (0 until user2Levels.size).random()
                user2CurrentLevel = user2Levels.removeAt(random)
            }
        }
    }

    private fun checkIfWon(user: Session): Boolean {
        return if(user == user1.session) {
            user1Levels.size == 0
        } else {
            user2Levels.size == 0
        }
    }

    private fun onHeartbeat(user: Session) {
        val socket = sessionToSocket(user)

        socket.sendMessage("heartbeat")
    }

    private fun onDone(user: Session) {
        val socket = sessionToSocket(user)

        if(user1.session == user) {
            user1Score += roomConfig.getLevelScore(user1CurrentLevel!!)
        } else {
            user2Score += roomConfig.getLevelScore(user2CurrentLevel!!)
        }
        updateScores()

        if(checkIfWon(user)) {
            socket.sendMessage("win")
            emit(socket.session, "lose")
            destroy()
        } else {
            CoroutineDispatcher.dispatch {
                socket.sendMessage("wait")

                delay(5000)

                randomLevel(socket)

                if(user == user1.session) {
                    socket.sendMessage("new_level", Gson().toJson(user1CurrentLevel))
                } else {
                    socket.sendMessage("new_level", Gson().toJson(user2CurrentLevel))
                }
            }
        }
    }

    private fun onPowerup(user: Session, message: Message) {
        val msg = Message("powerup_target", message.data)

        emit(user, msg)
    }

    private fun updateScores() {
        user1.sendMessage("progress_bar", JSONObject().put("you", user1Score).put("enemy", user2Score).toString())
        user2.sendMessage("progress_bar", JSONObject().put("you", user2Score).put("enemy", user1Score).toString())
    }
}