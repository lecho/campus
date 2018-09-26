package lecho.app.campus.plodz.tmp

import lecho.app.campus.plodz.viewmodel.AllPois
import lecho.app.campus.plodz.viewmodel.LatLong
import lecho.app.campus.plodz.viewmodel.PoiSummary

object MockDatabase {

    private val poisSummaries: List<PoiSummary> = listOf(
            PoiSummary(symbol = "B1",
                    name = "Name1",
                    shortName = "N1",
                    picName = "placeholder",
                    latLong = LatLong(54.1, 22.1)),
            PoiSummary(symbol = "B2",
                    name = "Name2",
                    shortName = "N2",
                    picName = "placeholder",
                    latLong = LatLong(54.2, 22.2)),
            PoiSummary(symbol = "B3",
                    name = "Name3",
                    shortName = "N3",
                    picName = "placeholder",
                    latLong = LatLong(54.3, 22.3)),
            PoiSummary(symbol = "B4",
                    name = "Name4",
                    shortName = "N4",
                    picName = "placeholder",
                    latLong = LatLong(54.4, 22.4)),
            PoiSummary(symbol = "B5",
                    name = "Name5",
                    shortName = "N5",
                    picName = "placeholder",
                    latLong = LatLong(54.5, 22.5)),
            PoiSummary(symbol = "B6",
                    name = "Name6",
                    shortName = "N6",
                    picName = "placeholder",
                    latLong = LatLong(54.6, 22.6))
    )

    val allPois: AllPois = AllPois(poisSummaries)
}