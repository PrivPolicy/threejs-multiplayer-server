package main.kotlin.fk_ms_yearly_project.model

object RoomConfigLibrary {
    private val roomConfigs: MutableList<RoomConfig> = mutableListOf(
        RoomConfig(2,3,2,5,11,13),
        RoomConfig(6,3,1,5,7,9),
        RoomConfig(3,5,3,4,7,9)
    )

    fun getRandom(): RoomConfig {
        return roomConfigs.random()
    }
}