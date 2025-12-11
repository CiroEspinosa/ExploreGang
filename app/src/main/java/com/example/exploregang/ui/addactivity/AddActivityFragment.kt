package com.example.exploregang.ui.addActivity

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.repository.ActivityRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.uploadUser
import com.example.exploregang.databinding.FragmentAddActivityBinding
import com.example.exploregang.util.Extensions.showError
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.abrirGaleria
import com.example.exploregang.util.Utils.showDateTimePickerDialogWithExactTime
import com.example.exploregang.util.Utils.stringToDateWithHour
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

import java.util.*


class AddActivityFragment : Fragment() {
    private var imageId: String? = ""
    private lateinit var mapView: View
    private lateinit var map: GoogleMap
    private lateinit var selectedAdress: String
    private lateinit var selectedLatLng: LatLng
    private lateinit var binding: FragmentAddActivityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddActivityBinding.inflate(layoutInflater)
        binding.apply {
            mapView = binding.root.findViewById(R.id.mapFragment)
            mapView.isVisible = false
            view.isGone = true
            btnCloseMap.isGone = true
            btnCreate.isVisible = true
            btnSearch.setOnClickListener {
                mapView.isVisible = true
                etLocationP.requestFocus()
                view.isVisible = true
                btnCloseMap.isVisible = true
                moveMapCameraAddress(etLocationP.text.toString())

            }
            btnCloseMap.setOnClickListener {
                mapView.isVisible = false
                view.isGone = true
                btnCloseMap.isGone = true
                btnCreate.isVisible = true
            }
            btnBackProfile.setOnClickListener {
                NavHostFragment.findNavController(
                    requireParentFragment()
                ).navigateUp()
            }

            etStartdateP.isFocusable = false
            etEnddateP.isFocusable = false
            etStartdateP.setOnClickListener {
                showDateTimePickerDialogWithExactTime(
                    etStartdateP,
                    requireContext()
                )
            }
            etEnddateP.setOnClickListener {
                showDateTimePickerDialogWithExactTime(
                    etEnddateP,
                    requireContext()
                )
            }
            btnCreate.setOnClickListener {
                moveMapCameraAddress(etLocationP.text.toString())
                if (checkFields()) {
                    loadingP.isVisible = true
                    val activity = Actividad(
                        name = etNameP.text.toString(),
                        address = etLocationP.text.toString(),
                        description = etDescriptionP.text.toString(),
                        startDate = stringToDateWithHour(etStartdateP.text.toString()),
                        endDate = stringToDateWithHour(etEnddateP.text.toString()),
                        imageId =imageId ,
                        creationDate = Date(),
                        isNumberParticipantsVisible = cbIsVisible.isChecked,
                    )
                    if (etMaxP.text.toString().isNotEmpty()) {
                        activity.maxParticipants = etMaxP.text.toString().toInt()
                    }
                    if (etMinP.text.toString().isNotEmpty()) {
                        activity.minParticipants = etMinP.text.toString().toInt()
                    }

                    NavHostFragment.findNavController(this@AddActivityFragment).navigateUp()
                    Toast.makeText(
                        requireContext(),
                        R.string.activity_created_successfully,
                        Toast.LENGTH_SHORT
                    ).show()

                    ActivityRepository.uploadActivity(activity, { activity ->
                        //currentUser!!.ownActivities!!.add(activity.id!!)
                        uploadUser(currentUser!!)
                    })
                }
            }
            ivActivityPhoto.setOnClickListener {
                abrirGaleria(pickMedia)
            }
            ivDeletePhoto.setOnClickListener {
                 imageId= ""
                ivActivityPhoto.setImageResource(R.drawable.usericon)
                ivActivityPhoto.setPadding(Utils.dpToPx(20, resources))
                ivDeletePhoto.isVisible = false
            }
            ivDeletePhoto.isVisible = false

        }

        initMapFragment()

        return binding.root
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Utils.setImageWithUri(uri,binding.ivActivityPhoto){
                binding.ivDeletePhoto.isVisible = true
                binding.ivDeletePhoto.setOnClickListener {
                    binding.ivDeletePhoto.isVisible = false
                    imageId= ""
                    binding.ivActivityPhoto.setImageResource(R.drawable.ic_camera)
                    binding.ivActivityPhoto.setPadding(Utils.dpToPx(20, resources))
                }
            }
            Utils.saveImage(uri) { imageId: String? -> this.imageId = imageId }
        } else {
        }
    }
    private fun moveMapCameraAddress(addressString: String) {
        if (addressString.isNotEmpty()) {
            val location = addressString
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName(location, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                selectedAdress = addressString
                selectedLatLng = latLng
                map.clear()
                map.addMarker(MarkerOptions().position(latLng))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))

            }
        }
    }

    private fun initMapFragment() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->

            map = googleMap

            val defaultLocation = LatLng(36.716667, -4.416667)
            selectedLatLng = defaultLocation
            moveMapCamera(defaultLocation)
            binding.etLocationP.setText("")
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMapToolbarEnabled = true
            googleMap.uiSettings.isScrollGesturesEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
            googleMap.uiSettings.isRotateGesturesEnabled = true
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

            googleMap.setOnMapClickListener { latLng ->
                moveMapCamera(latLng)
                selectedLatLng = latLng
            }
        }
    }


    private fun checkFields(): Boolean {
        binding.apply {
            if (etNameP.text!!.isEmpty()) {
                etNameP.showError(tilNameP, R.string.name_empty)
                return false
            }

            if (etMaxP.text.toString().isNotEmpty() && etMinP.text.toString().isNotEmpty()) {
                val min: Int = etMinP.text.toString().toInt()
                val max = etMaxP.text.toString().toInt()
                if (min > max) {
                    etMaxP.showError(tilMaxP, R.string.error_min_max)
                    etMinP.showError(tilMaxP, R.string.error_min_max)
                    return false
                }
            }

            if (etStartdateP.text.toString().isEmpty()) {
                etStartdateP.showError(tilStartdateP, R.string.register_message_select_date)
                return false
            }

            if (etEnddateP.text.toString().isEmpty()) {
                etEnddateP.showError(tilEnddateP, R.string.register_message_select_date_end)
                return false
            }

            if (etStartdateP.text.toString().isNotEmpty() && etEnddateP.text.toString()
                    .isNotEmpty()
            ) {
                val startDate: Date? = stringToDateWithHour(etStartdateP.text.toString())
                val endDate = stringToDateWithHour(etEnddateP.text.toString())

                if (startDate!!.before(Date())) {
                    etStartdateP.showError(tilStartdateP, R.string.date_past)
                    return false
                }

                if (startDate.after(endDate)) {
                    etEnddateP.showError(tilEnddateP, R.string.start_date_after_end)
                    return false
                }

                val startDateText = etStartdateP.text.toString()
                val endDateText = etEnddateP.text.toString()
                val equals = startDateText == endDateText

                if (equals) {
                    etEnddateP.showError(tilEnddateP, R.string.dates_are_same)
                    return false
                }
            }

            return true
        }
    }




    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.navigation_view)?.visibility =
            View.GONE
    }

    private fun moveMapCamera(latLng: LatLng) {

        map.clear()
        map.addMarker(MarkerOptions().position(latLng))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        binding.etLocationP.setText(latLng.toString()) // Mostrar la ubicación en el EditText
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses!!.isNotEmpty()) {
            val address = addresses!![0]
            val fullAddress = address.getAddressLine(0)

            // Actualizar el EditText con la dirección obtenida
            binding.etLocationP.setText(fullAddress)
            selectedAdress = fullAddress
        }
    }


}

