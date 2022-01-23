package main.kotlin.fk_ms_yearly_project

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import main.kotlin.fk_ms_yearly_project.controller.DatabaseManagerImpl
import main.kotlin.fk_ms_yearly_project.model.Level
import main.kotlin.fk_ms_yearly_project.model.SocketHandler
import spark.Request
import spark.Response
import spark.Spark.path
import spark.Spark.webSocket
import spark.kotlin.*
import java.io.File
import java.lang.StringBuilder
import java.lang.reflect.Type

val levelType: Type = object : TypeToken<Level>() {}.type
const val editorToken = "TOKEN GOES HERE :)"

fun main() {
    runBlocking {
        port(getHerokuPort())
        webSocket("/wss", SocketHandler::class.java)

        staticFiles.location("/public")

        get("/editor") { response.redirect("/edytor.html") }

        path("/level") {
            get("/all") { getAllLevels(request, response) }
            get("/:id") { getLevel(request, response) }

            post("/") { addLevel(request, response) }
            put("/:id") { updateLevel(request, response) }
            delete("/:id") { deleteLevel(request, response) }
        }

        get("/test/:id") { sendTest(request, response)}

        after {
            response.header("Access-Control-Allow-Origin", "*")
            response.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
            response.header("Access-Control-Allow-Headers", "*")
        }
    }
}

fun addLevel(request: Request, response: Response): String {
    val level = Gson().fromJson<Level>(request.body(), levelType)

    val token = request.headers("Authorization").split(" ")[1]
    if(token != editorToken) { return "You don't have permissions to access this resource" }

    DatabaseManagerImpl.addLevel(level)

    response.type("text/plain")
    return "Level added"
}

fun getLevel(request: Request, response: Response): String {
    response.type("application/json")
    return Gson().toJson(DatabaseManagerImpl.getLevel(request.params("id").toInt()))
}

fun updateLevel(request: Request, response: Response): String {
    val level = Gson().fromJson<Level>(request.body(), levelType)

    val token = request.headers("Authorization").split(" ")[1]
    if(token != editorToken) { return "You don't have permissions to access this resource" }

    DatabaseManagerImpl.updateLevel(request.params("id").toInt(), level)

    response.type("text/plain")
    return "Level updated"
}

fun deleteLevel(request: Request, response: Response): String {
    DatabaseManagerImpl.deleteLevel(request.params("id").toInt())

    val token = request.headers("Authorization").split(" ")[1]
    if(token != editorToken) { return "You don't have permissions to access this resource" }

    response.type("text/plain")
    return "Level deleted"
}

fun sendTest(request: Request, response: Response): String {
//    val file = File(Thread.currentThread().contextClassLoader.getResource("/public/test/index.html")!!.toURI())
    val file = File(Thread.currentThread().contextClassLoader.getResource("public/test/index.html")!!.toURI())
    println(file.absolutePath)

    val sb = StringBuilder()
    file.forEachLine {
        sb.append(it)
    }

    response.type("text/html")
    return sb.toString()
}

fun getAllLevels(request: Request, response: Response): String {
    response.type("application/json")
    return Gson().toJson(DatabaseManagerImpl.getAllLevels())
}

fun getHerokuPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        processBuilder.environment()["PORT"]!!.toInt()
    } else 5000
}