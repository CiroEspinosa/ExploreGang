package com.example.exploregang.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.text.Editable
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    //region Cargar y guardar imagenes

    fun getImage(imageId: String?, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().reference
        if (imageId != null) {
            val imageRef = storageReference.child(imageId)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                setImageWithUri(uri, imageView)
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
        }
    }

    fun saveImage(uri: Uri, onSuccess: (imageId: String?) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Obtener el ID del objeto una vez que la tarea de carga se complete correctamente
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val imageId = imageRef.path // O cualquier otra forma de obtener el ID deseado
                onSuccess(imageId)
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }

        }.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }

    fun setImageWithUri(uri: Uri, imageView: ImageView, function: () -> Unit = {}) {
        Glide.with(imageView.context)
            .load(uri)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
        imageView.isVisible = true
        imageView.setPadding(0)
        function()
    }

    fun abrirGaleria(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    //endregion


    //region Validaciones
    fun isPasswordValid(newPassword: String): Boolean {
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,30}\$".toRegex()
        return regex.matches(newPassword)
    }

    private fun isEmailValid(email: String?): Boolean {
        val regex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return regex.matches(email!!)
    }

    fun isPhoneValid(numero: String?): Boolean {
        val regex = Regex("^[679]\\d{8}$")
        val isValid = regex.matches(numero ?: "")
        return isValid
    }

    fun isAdult(dob: Date): Boolean {
        val adult = Calendar.getInstance()
        adult.time = dob
        adult.add(Calendar.YEAR, 14)
        val isAdult = adult.before(Calendar.getInstance())
        return isAdult
    }

    fun validar(dni: String): Boolean {
        if (dni.length != 9 || !Character.isLetter(dni[8])) {
            return false
        }
        val letraMayuscula: String =
            dni.substring(8)
                .uppercase(Locale.getDefault())
        return soloNumeros(dni) && letraDNI(dni) == letraMayuscula
    }

    private fun soloNumeros(dni: String): Boolean {
        var j: Int
        var numero: String
        var miDNI = ""
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
        val miDNI: Int = dni.substring(0, 8).toInt()
        val miLetra: String
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
        val resto: Int = miDNI % 23
        miLetra = asignacionLetra[resto]
        return miLetra
    }

    //endregion


    //region Fechas
    fun dateToString(date: Date?): String? {
        if (date == null) {
            return ""
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }


    fun showDateTimePickerDialogWithExactTime(editText: TextInputEditText, context: Context) {
        var selectedDateTime: Date
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDateTime = calendar.time
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val calendar = Calendar.getInstance()
                        calendar.time = selectedDateTime
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        selectedDateTime = calendar.time
                        editText.text = Editable.Factory.getInstance().newEditable(
                            SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                Locale.getDefault()
                            ).format(selectedDateTime)
                        )
                    },
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun showDateTimePickerDialog(editText: TextInputEditText, context: Context) {
        var selectedDateTime: Date
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDateTime = calendar.time
                editText.text = Editable.Factory.getInstance().newEditable(
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(selectedDateTime)
                )
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun stringToDateWithHour(dateString: String?): Date? {
        if (dateString.isNullOrEmpty()) {
            return null
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.parse(dateString)
    }

    fun stringToDate(dateString: String?): Date? {
        if (dateString.isNullOrEmpty()) {
            return null
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.parse(dateString)
    }

    fun dateToStringWithHour(date: Date?): String? {
        if (date == null) {
            return ""
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
    //endregion


    //region Notificaciones
    fun cancelNotification(activityId: String, context: Context) {
        val notificationId = getIntWithActivityId(activityId)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }

    fun getIntWithActivityId(activityId: String): Int {
        val subString = activityId.substring(activityId.length / 2 + 4)
        val id = subString.replace(Regex("[^0-9]"), "").toInt()
        return id.hashCode()
    }
    //endregion

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Verificar el estado de la conexión en función de la versión de Android
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    fun dpToPx(dp: Int, resources: Resources): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }



}