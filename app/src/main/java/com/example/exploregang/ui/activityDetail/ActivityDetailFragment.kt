package com.example.exploregang.ui.activityDetail

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.example.exploregang.R
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.prefs.UserPrefsManager
import com.example.exploregang.data.repository.ActivityRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.logout
import com.example.exploregang.data.repository.UserRepository.uploadUser
import com.example.exploregang.databinding.FragmentActivityDetailBinding
import com.example.exploregang.dialog.customDialog.CustomDialog
import com.example.exploregang.util.Constants.collectionActivity
import com.example.exploregang.util.Constants.notificationType
import com.example.exploregang.util.NotificationReceiver
import com.example.exploregang.util.Utils
import com.example.exploregang.util.Utils.cancelNotification
import com.example.exploregang.util.Utils.dateToStringWithHour
import com.example.exploregang.util.Utils.isInternetAvailable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


class ActivityDetailFragment : Fragment() {
    private lateinit var binding: FragmentActivityDetailBinding
    private lateinit var activity: Actividad
    private lateinit var mapView: View
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireArguments().getParcelable(collectionActivity)!!

        binding = FragmentActivityDetailBinding.inflate(layoutInflater)
        initUi()

        ActivityRepository.getActivity(activity.id!!, { activity ->
                this.activity = activity
                initUi()
            }, {})
        return binding.root
    }

    private fun initUi() {
        binding.apply {
            if (!activity.imageId.isNullOrEmpty()) {
                Utils.getImage(activity.imageId,binding.ivActivityPhoto)
            } else {
                ivActivityPhoto.setImageResource(R.drawable.activities3)
            }
            if( binding.root.findViewById<View>(R.id.mapFragment)!=null){
                mapView = binding.root.findViewById(R.id.mapFragment)
            }

            mapView.isGone = true
            view.isGone = true
            clMap.isVisible=false
            btnCloseMap.isGone = true
            btnJoin.isVisible = true
            if (activity.isNumberParticipantsVisible == false || activity.isNumberParticipantsVisible == null) {
                tvNumParticipants.isGone = true
                tilNumP.isGone = true
                tilMaxP.isGone = true
                tilMinP.isGone = true
            }else {
                etNumP.setText(activity.participantsIds.size.toString())
            }
            if (activity.maxParticipants == null) {
                tilMaxP.isGone = true
            } else {
                etMaxP.setText(activity.maxParticipants.toString())
            }
            if (activity.minParticipants == null) {
                tilMinP.isGone = true
            } else {
                etMinP.setText(activity.minParticipants.toString())
            }
            if (isInternetAvailable(requireContext())) {
                initMapFragment()

            } else {
                btnJoin.isGone = true
                btnEdit.isGone = true
                btnSearch.isGone = true

            }


            btnSearch.setOnClickListener {
                etLocationP.requestFocus()
                clMap.isVisible=true
                mapView.isVisible=true
                view.isVisible = true
                btnCloseMap.isVisible = true
                if (etLocationP.text.toString().isNotEmpty()) {
                    val location = etLocationP.text.toString()
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocationName(location, 1)
                    if (addresses!!.isNotEmpty()) {
                        val address = addresses[0]
                        val latLng = LatLng(address.latitude, address.longitude)

                        map.clear()
                        map.addMarker(MarkerOptions().position(latLng))
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))

                    }
                }
            }
            btnCloseMap.setOnClickListener {
                mapView.isGone = true
                view.isGone = true
                btnCloseMap.isGone = true
                btnJoin.isVisible = true
            }
            btnBackProfile.setOnClickListener {
                NavHostFragment.findNavController(this@ActivityDetailFragment).navigateUp()
            }
            activity.apply {
                if (currentUser != null) {
                    if (currentUser!!.id != creator!!.id) {
                        if (participantsIds.contains(currentUser!!.id)) {
                            seeParticipantsAndLeft()
                        } else {
                            btnJoin.setOnClickListener {
                                onClickJoinDef()
                            }
                        }


                    } else {
                        btnEdit.isVisible = true
                        btnEdit.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putParcelable(collectionActivity, activity)
                            NavHostFragment.findNavController(this@ActivityDetailFragment).navigate(
                                R.id.action_activityDetailFragment_to_editActivityFragment,
                                bundle
                            )
                        }
                        seeParticipantsAndDelete()
                    }
                } else {
                    btnJoin.setOnClickListener {
                        showDialogLogin()
                    }
                }

                if (activity.maxParticipants != null) {
                    if (currentUser != null) {
                        if (activity.participantsIds.size >= activity.maxParticipants!! && !activity.participantsIds.contains(
                                currentUser!!.id
                            )
                        ) {
                            btnJoin.isGone = true
                            tvDisenroll.setText(R.string.max_reached)
                            tvDisenroll.setOnClickListener { }
                        }
                    }
                }
                etNameP.setText(name)
                etDescriptionP.setText(description)
                etEnddateP.setText(dateToStringWithHour(endDate))
                etStartdateP.setText(dateToStringWithHour(startDate))
                etLocationP.setText(address)
                if (address.isNullOrEmpty()) {
                    // etLocationP.setText(lat.toString() + ", " + lon.toString())
                }
                etInfo.setText(
                    resources.getString(R.string.created_by) + " " + creator!!.email + " " + resources.getString(
                        R.string.on_date
                    ) + " " + dateToStringWithHour(creationDate)
                )



            }
        }
    }

    private fun showDialogLogin() {
        val dialog = CustomDialog()
        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.go_to_login)
            binding.acceptBtnCd.setOnClickListener {
                dialog.dismiss()
                UserPrefsManager.quitUserSession(requireContext())
                logout()
                NavHostFragment.findNavController(this@ActivityDetailFragment)
                    .navigate(R.id.action_activityDetailFragment_to_fragmentLogin)
            }
        }
        dialog.title = R.string.log_in
        dialog.message = R.string.user_login_necesary
        dialog.show(parentFragmentManager, "")
    }

    private fun onClickJoinDef() {
        binding.apply {
            activity.participantsIds.add(currentUser!!.id!!)
            currentUser!!.enrolledActivities!!.add(activity.id!!)
            uploadUser(currentUser!!, {}, {})
            ActivityRepository.uploadActivity(activity, {}, {})
            if (activity.startDate!!.after(Date())) {
                Toast.makeText(
                    requireContext(),
                    R.string.notification_advert,
                    Toast.LENGTH_SHORT
                ).show()
                loadingP.isVisible = false
                initNotifications(activity)
                NavHostFragment.findNavController(this@ActivityDetailFragment).navigateUp()
            } else {
                NavHostFragment.findNavController(this@ActivityDetailFragment).navigateUp()
            }

        }
    }

    private fun initNotifications(activity: Actividad) {
        val startDate = activity.startDate
        val startDateMillis = startDate!!.time
        val id = Utils.getIntWithActivityId(activity.id!!)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntentBefore = Intent(requireContext(), NotificationReceiver::class.java)
        notificationIntentBefore.putExtra(collectionActivity, activity)
        notificationIntentBefore.putExtra(
            notificationType,
            "before"
        ) // Agrega una clave para identificar el tipo de notificación
        val pendingIntentBefore = PendingIntent.getBroadcast(
            requireContext(),
            id,
            notificationIntentBefore,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeBeforeMillis = startDateMillis - (60 * 60 * 1000) // Resta una hora a la startDate
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeBeforeMillis,
            pendingIntentBefore
        )

        val notificationIntentExact = Intent(requireContext(), NotificationReceiver::class.java)
        notificationIntentExact.putExtra(collectionActivity, activity)
        notificationIntentExact.putExtra("notificationType", "exact")


        val pendingIntentExact = PendingIntent.getBroadcast(
            requireContext(),
            id,
            notificationIntentExact,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            startDateMillis,
            pendingIntentExact
        )

    }

    private fun showDialogDelete() {
        val dialog = CustomDialog()

        dialog.onBind = { binding ->
            binding.acceptBtnCd.isVisible = true
            binding.acceptBtnCd.setText(R.string.delete)
            dialog.onDismiss = { this.binding.loadingP.isVisible = false }
            binding.acceptBtnCd.setOnClickListener {
                dialog.dismiss()
                currentUser!!.ownActivities.remove(activity.id)
                uploadUser(currentUser!!)
                ActivityRepository.deleteActivity(activity, {}, {})
                Toast.makeText(
                    requireContext(),
                    R.string.activity_deleted,
                    Toast.LENGTH_SHORT
                ).show()
                NavHostFragment.findNavController(requireParentFragment())
                    .navigateUp()
            }
        }
        dialog.title = R.string.delete_activity
        dialog.message = R.string.adver_delete_activity
        dialog.show(parentFragmentManager, "")
    }

    private fun seeParticipantsAndDelete() {
        binding.apply {
            btnJoin.setText(R.string.see_participants)
            btnJoin.setOnClickListener { seeParticipants() }
            tvDisenroll.isVisible = true
            tvDisenroll.setText(R.string.delete_activity)
            tvDisenroll.setOnClickListener {
                showDialogDelete()
            }
        }
    }

    private fun seeParticipantsAndLeft() {
        binding.apply {
            btnJoin.setText(R.string.see_participants)
            btnJoin.setOnClickListener { seeParticipants() }
            if (!activity.isNumberParticipantsVisible!!) {
                btnJoin.isGone = true
            }
            tvDisenroll.isVisible = true
            tvDisenroll.setText(R.string.left_activity)
            tvDisenroll.setOnClickListener {
                activity.participantsIds.remove(currentUser!!.id!!)
                currentUser!!.enrolledActivities.remove(activity.id)
                uploadUser(currentUser!!, {}, {})
                ActivityRepository.uploadActivity(activity, {}, {})
                cancelNotification(activity.id!!, requireContext())
                etNumP.setText(activity.participantsIds.size.toString())

                btnJoin.isGone = true
                tvDisenroll.isGone = true
                Toast.makeText(requireContext(), R.string.disenroll_info, Toast.LENGTH_SHORT).show()


            }
        }
    }

    private fun seeParticipants() {
        val bundle = Bundle()
        bundle.putParcelable(collectionActivity, activity)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_activityDetailFragment_to_otherUserListFragment, bundle)
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
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses!!.isNotEmpty()) {
            val address = addresses!![0]
            val fullAddress = address.getAddressLine(0)
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
            moveMapCamera(defaultLocation)
            val defaultAddress = activity.address
            if (!defaultAddress.isNullOrEmpty()) {
                moveMapCameraAddress(defaultAddress)
            }
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMapToolbarEnabled = true
            googleMap.uiSettings.isScrollGesturesEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true
            googleMap.uiSettings.isRotateGesturesEnabled = true
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

            /*googleMap.setOnMapClickListener { latLng ->
                moveMapCamera(latLng)
            }*/
        }
    }
}