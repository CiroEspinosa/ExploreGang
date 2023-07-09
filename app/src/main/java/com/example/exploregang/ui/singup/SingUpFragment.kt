package com.example.exploregang.ui.singup


import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.repository.UserSession
import com.example.exploregang.databinding.FragmentSingUpBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class SingUpFragment : Fragment() {

    private var phone: String = ""
    private var dni: String = ""
    private var birthDate: Date = Date()
    private var tutorEmail: String = ""
    private var tutorName: String = ""
    private var passwordRepeat: String = ""
    private var password: String = ""
    private var nick: String = ""
    private var name: String = ""
    private var surname: String = ""
    private var email: String = ""

    private lateinit var binding: FragmentSingUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSingUpBinding.inflate(layoutInflater)
        binding.apply {
            btnBack.setOnClickListener {
                NavHostFragment.findNavController(requireParentFragment()).navigateUp()
            }
            btnSingUp.setOnClickListener {
                singUp()
            }
            val currentDate = Calendar.getInstance().time
            etDob.tag = currentDate

            etDob.setOnClickListener {
                val dateSelected = etDob.tag as? Date
                if (dateSelected != null) {
                    showDialogDate(dateSelected)


                }
            }
        }
        return binding.root
    }



    private fun singUp() {
        if (checkSingUp()) {
            UserSession.registerUser(email, password,requireContext(),this,binding)
        }
    }

    private fun checkSingUp(): Boolean {
        binding.apply {
            name = etName.text.toString().trim()
            nick = etNick.text.toString().trim()
            surname = etSurname.text.toString().trim()
            email = etEmail.text.toString().trim()
            password = etPassword.text.toString().trim()
            passwordRepeat = etPasswordRepeat.getText().toString().trim()

            tutorName = etTutorName.getText().toString()
            tutorEmail = etTutorEmail.getText().toString()
            birthDate = etDob.tag as Date
            dni = etDni.text.toString()
            phone = etPhone.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), R.string.name_empty, Toast.LENGTH_LONG).show()
                etName.requestFocus()
                return false
            }

            // check surname
            if (surname.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.register_message_surname_empty,
                    Toast.LENGTH_LONG
                )
                    .show()
                etSurname.requestFocus()
                return false
            }

            // nick
            if (nick.isEmpty()) {
                Toast.makeText(requireContext(), R.string.nick_empty, Toast.LENGTH_LONG)
                    .show()
                etNick.requestFocus()
                return false
            }


            // check dni
            if (dni.isNotEmpty()) {
                if (!validar(dni)) {
                    Toast.makeText(requireContext(), R.string.dni_invalid, Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
            }

            // check dob
            if (etDob.tag != null && etDob.tag is Date) {
                if (birthDate.after(Date())) {
                    Toast.makeText(requireContext(), R.string.dob_future, Toast.LENGTH_LONG).show()
                    etDob.requestFocus()
                    return false
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.register_message_select_dob,
                    Toast.LENGTH_LONG
                ).show()
                etDob.requestFocus()
                return false
            }
            if (phone.length < 4 && !llTutor.isVisible) {
                Toast.makeText(requireContext(), R.string.phone_short, Toast.LENGTH_LONG).show()
                etPhone.requestFocus()
                return false
            }
            if (email.length < 3) {
                Toast.makeText(
                    requireContext(),
                    R.string.register_message_email_short,
                    Toast.LENGTH_LONG
                ).show()
                binding.etEmail.requestFocus()
                return false
            }
            if (!email.contains("@")) {
                Toast.makeText(
                    requireContext(),
                    R.string.register_message_email_incorrect,
                    Toast.LENGTH_LONG
                ).show()
                etEmail.requestFocus()
                return false
            }
            if (password.length < 6) {
                Toast.makeText(
                    requireContext(),
                    R.string.invalid_password,
                    Toast.LENGTH_LONG
                ).show()
                etPassword.requestFocus()
                return false
            }

            // check password equals
            if (!password.equals(passwordRepeat)) {
                Toast.makeText(
                    requireContext(),
                    R.string.register_message_passwords_different,
                    Toast.LENGTH_LONG
                ).show()
                etPasswordRepeat.requestFocus()
                return false
            }
            if (llTutor.isVisible) {
                if (tutorEmail.length < 3) {
                    Toast.makeText(
                        requireContext(),
                        R.string.register_message_email_short,
                        Toast.LENGTH_LONG
                    ).show()
                    etTutorEmail.requestFocus()
                    return false
                }
                // check tutor email @ character
                if (!tutorEmail.contains("@")) {
                    Toast.makeText(
                        requireContext(),
                        R.string.register_message_email_incorrect,
                        Toast.LENGTH_LONG
                    ).show()
                    etTutorEmail.requestFocus()
                    return false
                }
            }

            return true
        }
    }

    private fun showDialogDate(date: Date) {
        var fecha: Date? = date
        if (fecha == null) fecha = Date()
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = fecha
        val dialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date1: Date =
                        sdf.parse(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    val etBirthDate: EditText = binding.etDob
                    etBirthDate.setText(sdf.format(date1))
                    etBirthDate.tag = date1

                    if (date1.after(Date())) {
                        Toast.makeText(requireContext(), R.string.dob_future, Toast.LENGTH_SHORT)
                            .show()
                        binding.etDob.requestFocus()
                    }
                    var adult = Calendar.getInstance()
                    adult.time = binding.etDob.tag as? Date
                    adult.add(Calendar.YEAR, 18)
                    var now = Calendar.getInstance()
                    now.time = Date()
                    binding.llTutor.isVisible = adult.after(now)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    fun validar(dni: String): Boolean {
        var letraMayuscula = "" //Guardaremos la letra introducida en formato mayúscula

        // Aquí excluimos cadenas distintas a 9 caracteres que debe tener un dni y también si el último caracter no es una letra
        if (dni.length !== 9 || !Character.isLetter(dni[8])) {
            return false
        }


        // Al superar la primera restricción, la letra la pasamos a mayúscula
        letraMayuscula = dni.substring(8).uppercase(Locale.getDefault())

        // Por último validamos que sólo tengo 8 dígitos entre los 8 primeros caracteres y que la letra introducida es igual a la de la ecuación
        // Llamamos a los métodos privados de la clase soloNumeros() y letraDNI()
        return soloNumeros(dni) && letraDNI(dni) == letraMayuscula
    }

    private fun soloNumeros(dni: String): Boolean {
        var j: Int
        var numero =
            "" // Es el número que se comprueba uno a uno por si hay alguna letra entre los 8 primeros dígitos
        var miDNI = "" // Guardamos en una cadena los números para después calcular la letra
        val unoNueve = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        var i = 0
        while (i < dni.length - 1) {
            numero = dni.substring(i, i + 1)
            j = 0
            while (j < unoNueve.size) {
                if (numero == unoNueve[j]) {
                    miDNI += unoNueve[j]
                }
                j++
            }
            i++
        }
        return miDNI.length == 8
    }

    private fun letraDNI(dni: String): String {
        // El método es privado porque lo voy a usar internamente en esta clase, no se necesita fuera de ella

        // pasar miNumero a integer
        val miDNI: Int = dni.substring(0, 8).toInt()
        var resto = 0
        var miLetra = ""
        val asignacionLetra = arrayOf(
            "T",
            "R",
            "W",
            "A",
            "G",
            "M",
            "Y",
            "F",
            "P",
            "D",
            "X",
            "B",
            "N",
            "J",
            "Z",
            "S",
            "Q",
            "V",
            "H",
            "L",
            "C",
            "K",
            "E"
        )
        resto = miDNI % 23
        miLetra = asignacionLetra[resto]
        return miLetra
    }
}