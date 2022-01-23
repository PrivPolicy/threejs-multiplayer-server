package main.kotlin.fk_ms_yearly_project.model

import org.eclipse.jetty.websocket.api.Session
import org.json.JSONObject

class Socket(val session: Session) {
    var room: Room? = null

    val isRoom: Boolean
        get() {
            return room != null
        }

    val isOpen: Boolean
        get() {
            return session.isOpen
        }

    private fun sendString(message: String) {
        try {
            session.remote.sendString(message)
        } catch(e: Exception) {
            println(e.stackTrace)
            destroy()
        }
    }

    fun close() {
        session.close()
    }

    fun destroy() {
        if(isRoom) {
            room!!.onClose(session, 1000, "forfeit")
        }

        close()
    }

    fun sendMessage(title: String, data: String = "{}") {
        sendString(
            JSONObject()
            .put("title", title)
            .put("data", data)
            .toString()
        )
    }
}