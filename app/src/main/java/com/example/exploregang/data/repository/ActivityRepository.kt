package com.example.exploregang.data.repository

import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.model.User
import com.example.exploregang.util.Constants.collectionActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

object ActivityRepository {
    private val db = Firebase.firestore
    private val currentUser = UserRepository.currentUser
    var allActivities: ArrayList<Actividad> = ArrayList()
    var myActivities: ArrayList<Actividad> = ArrayList()

    fun uploadActivity(activity: Actividad, onSuccess: (activity:Actividad) -> Unit = {}, onError: () -> Unit = {}) {
        if(activity.id==null){
            activity.id= UUID.randomUUID().toString()
        }
        if (activity.creator == null) {
            val userCopy = UserRepository.currentUser?.let { user ->
                User(
                    id = user.id,
                    name = user.name,
                    imageId = user.imageId,
                    dob = user.dob,
                    phone = user.phone,
                    email = user.email,
                    ownActivities = ArrayList(user.ownActivities),
                    enrolledActivities = ArrayList(user.enrolledActivities),
                    creationDate = user.creationDate,
                    isPublic = user.isPublic,
                    contacts = ArrayList(user.contacts)
                )
            }

            activity.creator = userCopy
            activity.creator?.imageId = ""
        }
        db.collection(collectionActivity)
            .document(activity.id!!)
            .set(activity).addOnSuccessListener {
                onSuccess(activity)
            }.addOnFailureListener { e ->
                e.printStackTrace()
                onError()
            }
    }

    fun deleteActivity(activity: Actividad, onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        db.collection(collectionActivity)
            .document(activity.id!!)
            .delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError()
                }
            }
    }

    fun getMyActivities(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onNoData: () -> Unit = {}
    ) {
        db.collection(collectionActivity).whereEqualTo("user.email", currentUser!!.email)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        myActivities!!.add(document.toObject(Actividad::class.java))
                    }
                    if (myActivities.size == 0) {
                        onNoData()
                    } else {
                        onSuccess()
                    }
                } else {
                    onError()
                }
            }
    }

    fun getAllActivities(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onNoData: () -> Unit = {}
    ) {
        db.collection(collectionActivity).get().addOnSuccessListener { result ->
            allActivities.clear()
            for (document in result) {
                val activity = document.toObject(Actividad::class.java)
                allActivities.add(activity)
            }
            if (allActivities.size == 0) {
                onNoData()
            } else {
                onSuccess()
            }
        }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onError()
            }
    }

    fun getActivity(
        id: String,
        onSuccess: (activity: Actividad) -> Unit = {},
        onError: () -> Unit = {}
    ) {
        db.collection(collectionActivity)
            .document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val activity = documentSnapshot.toObject(Actividad::class.java)
                    onSuccess(activity!!)

                } else {
                    onError()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onError()
            }
    }

}