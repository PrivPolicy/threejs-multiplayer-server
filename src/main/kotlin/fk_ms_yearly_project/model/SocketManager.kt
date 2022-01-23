package main.kotlin.fk_ms_yearly_project.model

import java.util.concurrent.ConcurrentHashMap
import org.eclipse.jetty.websocket.api.Session

object SocketManager {
    val socketMap: MutableMap<Session, Socket> = ConcurrentHashMap()
    val rooms: MutableList<Room> = arrayListOf()

    fun broadcast(title: String, data: String = "{}") {
        socketMap.values.filter { obj: Socket -> obj.isOpen }
            .forEach { socket ->
                socket.sendMessage(title, data)
            }
    }

    fun matchMake() {
        val candidates = socketMap.values.filter { obj: Socket -> obj.isOpen && !obj.isRoom }.toMutableList()
        val candidatesLength = candidates.size
        var roomsToCreate = candidatesLength / 2

        while (roomsToCreate > 0) {
            var random = (0 until candidates.size).random()
            val candidate1 = candidates.removeAt(random)

            random = (0 until candidates.size).random()
            val candidate2 = candidates.removeAt(random)

            val room = Room(candidate1, candidate2)
            rooms.add(room)

            room.broadcast("room_assigned")

            roomsToCreate--
        }
    }
}

