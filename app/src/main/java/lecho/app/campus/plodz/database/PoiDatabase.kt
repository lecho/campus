package lecho.app.campus.plodz.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database


@Database(entities = [Poi::class, Faculty::class, Unit::class], version = 1, exportSchema = false)
abstract class PoiDatabase: RoomDatabase() {

}