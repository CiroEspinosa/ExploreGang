package com.example.exploregang.data.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class User(
    var id: String? = null,
    var name: String? = null,
    var imageId: String? = null,
    var dob: Date? = null,
    var phone: String? = null,
    var email: String? = null,
    var ownActivities: ArrayList<String> = ArrayList(),
    var enrolledActivities: ArrayList<String> = ArrayList(),
    var creationDate: Date? = null,
    var isPublic: Boolean? = null,
    var contacts: ArrayList<String> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        Date(parcel.readLong()), // Corrige la lectura de la fecha (dob)
        parcel.readString(),
        parcel.readString(),
        ArrayList<String>().apply { parcel.readStringList(this) }, // Corrige la lectura de las listas (ownActivities y enrolled)
        ArrayList<String>().apply { parcel.readStringList(this) },
        Date(parcel.readLong()), // Corrige la lectura de la fecha (creationDate)
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        ArrayList<String>().apply { parcel.readStringList(this) } // Corrige la lectura de la lista (contacts)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageId)
        parcel.writeLong(dob?.time ?: -1) // Corrige la escritura de la fecha (dob)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeStringList(ownActivities) // Corrige la escritura de las listas (ownActivities y enrolled)
        parcel.writeStringList(enrolledActivities)
        parcel.writeLong(creationDate?.time ?: -1) // Corrige la escritura de la fecha (creationDate)
        parcel.writeValue(isPublic)
        parcel.writeStringList(contacts) // Corrige la escritura de la lista (contacts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
