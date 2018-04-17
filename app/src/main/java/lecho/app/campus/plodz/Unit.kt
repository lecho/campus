package lecho.app.campus.plodz

/**
 * Unit like ICS or DMCS. Unit may belong to one faculty or exists without faculty.
 */
data class Unit(val id: Long,
                val name: String,
                val shortName: String,
                val filterableName: String)