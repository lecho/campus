package lecho.app.campus.plodz.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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