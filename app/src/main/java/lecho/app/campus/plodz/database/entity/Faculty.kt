package lecho.app.campus.plodz.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Poi::class,
        parentColumns = ["id"],
        childColumns = ["poiId"],
        onDelete = ForeignKey.NO_ACTION)])
data class Faculty(@PrimaryKey val id: Long,
                   val name: String,
                   val shortName: String,
                   val filterableName: String,
                   val poiId: Long)