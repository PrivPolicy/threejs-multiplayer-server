package main.kotlin.fk_ms_yearly_project.model

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.*

@WebSocket
class SocketHandler {

    @OnWebSocketConnect
    fun onConnect(user: Session) {
        val socket = Socket(user)
        SocketManager.socketMap[user] = socket

        SocketManager.matchMake()
        println("User connected, size = ${SocketManager.socketMap.size}")
    }

    @OnWebSocketClose
    fun onClose(user: Session, statusCode: Int, reason: String?) {
        val socket = SocketManager.socketMap[user]!!

        if(socket.isRoom) {
            socket.room!!.onClose(user, statusCode, reason)
        }

        SocketManager.socketMap.remove(user)
        println("User disconnected, size = ${SocketManager.socketMap.size}")
    }

    @OnWebSocketMessage
    fun onMessage(user: Session, message: String) {
        val socket = SocketManager.socketMap[user]!!

        if(socket.isRoom) {
            socket.room!!.onMessage(user, message)
        }
    }
}