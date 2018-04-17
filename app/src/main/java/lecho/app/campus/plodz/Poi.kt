package lecho.app.campus.plodz

/**
 * Single building or other kind of object with specific location on the map
 */
data class Poi(val id: Long,
               val name: String,
               val symbol: String,
               val description: String,
               val latitude: Double,
               val longitude: Double,
               val hasImage: Boolean,
               val iconType: IconType,
               val categories: List<PoiCategory>,
               val units: List<Unit>)