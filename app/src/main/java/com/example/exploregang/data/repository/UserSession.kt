package com.example.exploregang.data.repository

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.model.User
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.databinding.FragmentLoginBinding
import com.example.exploregang.databinding.FragmentSingUpBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object UserSession {
    var userId: String? = null
    var username: String? = null
    var email: String? = null
    var isLoggedIn = false
    private lateinit var user:FirebaseUser
    private var userLiveData: MutableLiveData<User>? = null
    private lateinit var auth: FirebaseAuth

    fun saveUserSessionEmail(email: String) {
       UserSession.email=email
        isLoggedIn = true
    }

    fun logout() {
        userId = null
        username = null
        email=null
        isLoggedIn = false
    }
   fun registerUser(email: String, password: String ,context: Context, fragment: Fragment,binding:FragmentSingUpBinding) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {

                    user = Firebase.auth.currentUser!!
                    NavHostFragment.findNavController(
                        fragment
                    ).navigate(R.id.action_fragmentSingUp_to_fragmentHome)

                    // Realiza las operaciones adicionales que necesites, como guardar información adicional en la base de datos
                } else {
                    // El registro falló
                    Toast.makeText(
                       context,
                        "Error al registrar el usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
    fun loginUser(email: String, password: String, context: Context, fragment: Fragment,binding:FragmentLoginBinding) {
        auth= FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    // El inicio de sesión se realizó exitosamente

                     user = Firebase.auth.currentUser!!
                    if (binding.cbStaySignedIn.isChecked){
                        UserPrefsManager.setUserEmailPref(email,context)
                    }else{
                        UserPrefsManager.removeUserEmailPref(context)
                    }
                    saveUserSessionEmail(email)
                    NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_fragmentLogin_to_fragmentHome)

                    // Realiza las operaciones adicionales que necesites
                } else {
                    // El inicio de sesión falló
                    Toast.makeText(
                        context,
                        "Error al iniciar sesión",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loading.visibility = View.GONE
                }
            }
    }

    /**
     * Este método inicializa una variable MutableLiveData que contendrá información del usuario
     * @return
     */
    fun getUserLiveData(): MutableLiveData<User>? {
        if (userLiveData == null) {
            userLiveData = MutableLiveData()
        }
        return userLiveData
    }

}

