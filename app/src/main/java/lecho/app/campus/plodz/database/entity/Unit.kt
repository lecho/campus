package lecho.app.campus.plodz.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// TODO Add foreignKeys to index to avoid full table scan
/**
 * Unit like ICS or DMCS. Unit may belong to one faculty or exists without faculty.
 */
@Entity(foreignKeys = [
    ForeignKey(entity = Faculty::class,
            parentColumns = ["id"],
            childColumns = ["facultyId"],
            onDelete = ForeignKey.NO_ACTION),
    ForeignKey(entity = Poi::class,
            parentColumns = ["id"],
            childColumns = ["poiId"],
            onDelete = ForeignKey.NO_ACTION)])
data class Unit(@PrimaryKey val id: Long,
                val name: String,
                val shortName: String,
                val filterableName: String,
                val facultyId: Long, // Possible many to many relation
                val poiId: Long)