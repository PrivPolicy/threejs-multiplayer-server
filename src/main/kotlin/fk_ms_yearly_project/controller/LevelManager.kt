package main.kotlin.fk_ms_yearly_project.controller

import main.kotlin.fk_ms_yearly_project.model.Level

interface LevelManager {
    val level: Level
    fun get(): Level
    fun save(items: Level): Unit
}