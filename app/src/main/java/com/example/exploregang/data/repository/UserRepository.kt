package com.example.exploregang.data.repository

import com.example.exploregang.data.model.User
import com.example.exploregang.data.model.UserRegisterFields
import com.example.exploregang.util.Constants.collectionUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

object UserRepository {
    var currentUser: User? = null
    private val db = Firebase.firestore
    var userSession = FirebaseAuth.getInstance().currentUser
    var allUsers: ArrayList<User> = ArrayList()

    fun logout() {
        currentUser = null
        FirebaseAuth.getInstance().signOut()
    }

    fun makeUserWithUserFields(userRegisterFields: UserRegisterFields): User? {
        var user: User?
        userRegisterFields.apply {
            user = User(
                null,
                name!!,
                photo,
                email!!,
            )
        }
        return user
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit = {},
        onErrorChangePassword: () -> Unit = {},
        onErrorReautentication: () -> Unit = {}
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPassword)

        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { passwordUpdateTask ->
                    if (passwordUpdateTask.isSuccessful) {
                        onSuccess()
                    } else {
                        onErrorChangePassword()
                    }
                }
            } else {
                onErrorReautentication()
            }
        }

    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception?) -> Unit, onFailureDownload: () -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    getUserData(onSuccess, onFailureDownload)
                } else {
                    onFailure(task.exception)
                }
            }
    }


    fun deleteAccount(onSuccess: () -> Unit = {}, onFailure: (String?) -> Unit = {}) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        firebaseUser.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val db = FirebaseFirestore.getInstance()
                val userId = firebaseUser.uid
                db.collection(collectionUser).document(userId).delete()
                    .addOnSuccessListener {
                        onSuccess()
                        logout()
                    }
                    .addOnFailureListener {
                        onFailure(task.exception?.message)
                    }
            } else {
                // Log.d(task.exception?.message,"aaaaa")
                onFailure(task.exception?.message)
            }
        }
    }

    fun getUserData(onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser!!
            val userId = firebaseUser.uid
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection(collectionUser).document(userId)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    try {
                        currentUser = document.toObject<User>()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onFailure()
                    }

                    if (currentUser != null) {
                        onSuccess()
                    } else {
                        onFailure()
                    }
                } else {
                    onFailure()
                }
            }
                .addOnFailureListener {
                    onFailure()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure()
        }
    }

    fun singUpUser(
        userFileds: UserRegisterFields,
        onSuccess: () -> Unit = {},
        onFailureUpload: () -> Unit = {},
        onFailureSingUp: (Exception?) -> Unit = {}
    ) {
        val user: User? = makeUserWithUserFields(userFileds)
        this.currentUser = user
        val firebaseAuth = FirebaseAuth.getInstance()
        currentUser!!.apply {
            firebaseAuth.createUserWithEmailAndPassword(email!!, userFileds.password!!)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        uploadUser(currentUser!!, onSuccess, onFailureUpload)
                    } else {
                        val exception = task.exception
                        exception?.printStackTrace()
                        onFailureSingUp(exception)

                    }
                }
        }


    }


    fun uploadUser(user: User, onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        val database = Firebase.firestore
        val auth = Firebase.auth
        if (user.id == null) {
            user.id = auth.currentUser!!.uid
        }
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(user.name).build()
        auth.currentUser?.updateProfile(profileUpdates)?.addOnSuccessListener {
            database.collection(collectionUser).document(user.id!!).set(user)
                .addOnSuccessListener {
                    if (user.id == auth.currentUser!!.uid) {
                        getUserData({ onSuccess() }, {})
                    }else{

                    }

                }.addOnFailureListener {
                    it.printStackTrace()
                    onFailure()
                }
        }?.addOnFailureListener { exception ->
            exception.printStackTrace()
            onFailure()
        } ?: onFailure()
    }


    fun sendPasswordResetEmail(
        email: String?,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
        onNoEmail: () -> Unit = {}
    ) {
        if (email.isNullOrEmpty()) {
            onNoEmail()
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure()
                    }
                }
        }
    }

    fun getAllUsers(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onNoData: () -> Unit = {}
    ) {
        db.collection(collectionUser).get().addOnSuccessListener { result ->
            allUsers.clear()
            for (document in result) {
                val user = document.toObject(User::class.java)
                allUsers.add(user)
            }
            if (allUsers.size == 0) {
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
}





