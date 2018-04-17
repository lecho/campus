package lecho.app.campus.plodz

data class Faculty(val id: Long,
                   val name: String,
                   val shortName: String,
                   val filterableName: String,
                   val units: List<Unit>)