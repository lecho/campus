package lecho.app.campus.plodz.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Single building or other kind of object with specific location on the map
 */
@Entity
data class Poi(@PrimaryKey val id: Long,
               val name: String,
               val symbol: String,
               val description: String,
               val latitude: Double,
               val longitude: Double,
               val hasImage: Boolean)