package main.kotlin.fk_ms_yearly_project.controller

import main.kotlin.fk_ms_yearly_project.model.Level

class LevelManagerImpl: LevelManager {
    override var level: Level = Level()

    override fun get(): Level {
        return level
    }

    override fun save(items: Level) {
        level = items
    }

}