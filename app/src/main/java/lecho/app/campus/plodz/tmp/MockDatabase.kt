package lecho.app.campus.plodz.tmp

import lecho.app.campus.plodz.viewmodel.MapData
import lecho.app.campus.plodz.viewmodel.LatLong
import lecho.app.campus.plodz.viewmodel.PoiBaseInfo

object MockDatabase {

    private val POIS_BASE_INFOS: List<PoiBaseInfo> = listOf(
            PoiBaseInfo(symbol = "B1",
                    name = "Name1",
                    shortName = "N1",
                    picName = "placeholder",
                    latLong = LatLong(51.7454552,19.4523541)),
            PoiBaseInfo(symbol = "B2",
                    name = "Name2",
                    shortName = "N2",
                    picName = "placeholder",
                    latLong = LatLong(51.7465562,19.4503541)),
            PoiBaseInfo(symbol = "B3",
                    name = "Name3",
                    shortName = "N3",
                    picName = "placeholder",
                    latLong = LatLong(51.7476572,19.4523541)),
            PoiBaseInfo(symbol = "B4",
                    name = "Name4",
                    shortName = "N4",
                    picName = "placeholder",
                    latLong = LatLong(51.7497582,19.4533541)),
            PoiBaseInfo(symbol = "B5",
                    name = "Name5",
                    shortName = "N5",
                    picName = "placeholder",
                    latLong = LatLong(51.7508592,19.4523541)),
            PoiBaseInfo(symbol = "B6",
                    name = "Name6",
                    shortName = "N6",
                    picName = "placeholder",
                    latLong = LatLong(51.7429552,19.4563561))
    )

    val MAP_DATA: MapData = MapData(POIS_BASE_INFOS)
}