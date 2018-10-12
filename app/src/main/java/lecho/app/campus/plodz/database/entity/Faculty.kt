package lecho.app.campus.plodz.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Poi::class,
        parentColumns = ["id"],
        childColumns = ["poiId"],
        onDelete = ForeignKey.NO_ACTION)])
data class Faculty(@PrimaryKey val id: Long,
                   val name: String,
                   val shortName: String,
                   val filterableName: String,
                   val poiId: Long)