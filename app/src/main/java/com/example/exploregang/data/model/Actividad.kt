package com.example.exploregang.data.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.collections.ArrayList

data class Actividad(
    var id: String? = null,
    var name: String? = null,
    var creator: User? =null,
    var description: String? = null,
    var address: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var imageId: String? = null,
    var maxParticipants: Int? = null,
    var minParticipants: Int? = null,
    var participantsIds: ArrayList<String> = ArrayList(),
    var isNumberParticipantsVisible: Boolean? = null,
    var creationDate: Date? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        Date(parcel.readLong()), // Lee el valor como un Long y luego crea una instancia de Date
        Date(parcel.readLong()), // Lee el valor como un Long y luego crea una instancia de Date
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        ArrayList<String>().apply {
            parcel.readStringList(this)
        },
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        Date(parcel.readLong()) // Lee el valor como un Long y luego crea una instancia de Date
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeParcelable(creator, flags)
        parcel.writeString(description)
        parcel.writeString(address)
        parcel.writeLong(startDate?.time ?: -1) // Escribe el valor como un Long
        parcel.writeLong(endDate?.time ?: -1) // Escribe el valor como un Long
        parcel.writeString(imageId)
        parcel.writeValue(maxParticipants)
        parcel.writeValue(minParticipants)
        parcel.writeStringList(participantsIds)
        parcel.writeValue(isNumberParticipantsVisible)
        parcel.writeLong(creationDate?.time ?: -1) // Escribe el valor como un Long
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Actividad> {
        override fun createFromParcel(parcel: Parcel): Actividad {
            return Actividad(parcel)
        }

        override fun newArray(size: Int): Array<Actividad?> {
            return arrayOfNulls(size)
        }
    }
}
