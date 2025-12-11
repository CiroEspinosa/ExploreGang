package com.example.exploregang.ui.activityList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.repository.ActivityRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.data.repository.UserRepository.uploadUser
import com.example.exploregang.util.Constants
import com.example.exploregang.util.Utils.cancelNotification
import com.example.exploregang.util.Utils.dateToStringWithHour
import java.util.*
import kotlin.collections.ArrayList

class ActivityListViewModel() : ViewModel() {

    var activityList = mutableListOf<Actividad>()


    private val _result = MutableLiveData<ListResult>()
    val result: LiveData<ListResult>
        get() = _result

    fun orderByDateDes() {
        activityList.sortByDescending { it.startDate }
    }


    fun getAllActivitiesWithNoFilter(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = { },
        onNoData: () -> Unit = { }
    ) {
        _result.value = ListResult.LOADING

        ActivityRepository.getAllActivities(
            onSuccess = {

                activityList.clear()
                activityList.addAll(ActivityRepository.allActivities)
                _result.value = ListResult.SUCCESS
                onSuccess()
            },
            onError = {
                _result.value = ListResult.FAILURE
                onError()
            },
            onNoData = {
                _result.value = ListResult.NODATA
                onNoData()
            }
        )
    }


    fun getAllActivities(
        searchActivity: String = "",
        searchLocation: String = "",
        searchDate: String = ""
    ) {
        _result.value = ListResult.LOADING

        ActivityRepository.getAllActivities(
            {
                orderByDateDes()
                activityList.clear()
                activityList.addAll(ActivityRepository.allActivities)
                val aux = activityList.filter { activity ->
                    val containsSearchActivity = containsName(activity, searchActivity)
                    val containsSearchLocation = containsLocation(activity, searchLocation)
                    val containsSearchDate = containsDate(activity, searchDate)
                    (containsSearchActivity && containsSearchLocation && containsSearchDate)
                }
                activityList.clear()
                activityList.addAll(aux)
                if (activityList.isEmpty()) {
                    _result.value = ListResult.NODATA
                } else {
                    _result.value = ListResult.SUCCESS
                }
            },
            {
                _result.value = ListResult.FAILURE
            },
            {
                _result.value = ListResult.NODATA
            }
        )
    }

    private fun containsName(activity: Actividad, searchActivity: String): Boolean {
        if (searchActivity.isNullOrEmpty()) {
            return true
        }
        val isActivityNameContained = activity.name!!.contains(searchActivity, true)
        val isCreatorNameContained = activity.creator!!.name!!.contains(searchActivity, true)
        val isCreatorEmailContained=activity.creator!!.email!!.contains(searchActivity,false)
        val containsSearchName = isActivityNameContained || isCreatorNameContained||isCreatorEmailContained

        return containsSearchName
    }

    private fun containsLocation(activity: Actividad, searchLocation: String): Boolean {
        if (searchLocation.isNullOrEmpty()) {
            return true
        }
        val conAddress = activity.address!!.contains(searchLocation, true)
        return conAddress
    }

    private fun containsDate(activity: Actividad, searchDate: String): Boolean {
        if (searchDate.isNullOrEmpty()) {
            return true
        }
        val dateContains = (dateToStringWithHour(activity.startDate)!!.contains(searchDate))
        return dateContains
    }


    fun getNextActivities(context: Context) {
        _result.value = ListResult.LOADING

        ActivityRepository.getAllActivities(
            onSuccess = {


                activityList.clear()
                activityList.addAll(ActivityRepository.allActivities)
                detectDeletedActivities(context)
                val aux=activityList.filter { actividad ->
                    actividad.participantsIds.contains(currentUser!!.id)&&actividad.startDate!!.after(Date()) }
                activityList.clear()
                activityList.addAll(aux)
                orderByDateDes()
                if (activityList.isNotEmpty()) {
                    val nextdate = activityList[0]
                    activityList.clear()
                    activityList.add(nextdate)
                    _result.value=ListResult.SUCCESS
                  /*  var aux = activityList.filter { actividad ->
                        actividad.participantsIds.contains(currentUser!!.id) && (dateToString(
                            nextdate
                        ) == dateToString(actividad.startDate))
                    }
                    if (aux.isEmpty()) {
                        _result.value = ListResult.NODATA
                    } else {
                        activityList.clear()
                        activityList.addAll(aux)
                        _result.value = ListResult.SUCCESS
                    }*/
                }else{
                    _result.value = ListResult.NODATA
                }


            },
            onError = {
                _result.value = ListResult.FAILURE
            },
            onNoData = {
                _result.value = ListResult.NODATA
            }
        )
    }


    private fun detectDeletedActivities(context: Context) {
        //val enrolledAct = currentUser!!.enrolledActivities.size
        //val iterator = currentUser!!.enrolledActivities.iterator()
        /*while (iterator.hasNext()) {
            val id = iterator.next()
            var exists = false
            for (activity in activityList) {
                if (activity.id == id) {
                    exists = true
                    break
                }
            }
            if (!exists) {
                iterator.remove()
                cancelNotification(id, context)
            }
        }

        val iterator2 = currentUser!!.ownActivities.iterator()
        while (iterator2.hasNext()) {
            val id = iterator2.next()
            var exists = false
            for (activity in activityList) {
                if (activity.id == id) {
                    exists = true
                    break
                }
            }
            if (!exists) {
                iterator2.remove()
            }
        }
        uploadUser(currentUser!!, {}, {})
        if (currentUser!!.enrolledActivities.size < enrolledAct) {
            _result.value = ListResult.ACTIVITYDELETED
        }*/
    }




    fun getUserActivities(
        searchActivity: String,
        searchLocation: String,
        searchDate: String,
        list: String
    ) {
        _result.value = ListResult.LOADING
        val aux = ArrayList<Actividad>()
        ActivityRepository.getAllActivities(
            {
                aux.addAll(ActivityRepository.allActivities)
                val filteredList = aux.filter { activity ->
                    val containsSearchActivity = containsName(activity, searchActivity)
                    val containsSearchLocation = containsLocation(activity, searchLocation)
                    val containsSearchDate = containsDate(activity, searchDate)
                    var userActivity = false
                    if (list == Constants.owned) {
                        if (activity.creator!!.id == currentUser!!.id) {
                            userActivity = true
                        }
                    } else {
                        if (activity.participantsIds.contains(currentUser!!.id)) {
                            userActivity = true
                        }
                    }
                    (containsSearchActivity && containsSearchLocation && containsSearchDate && userActivity)
                }
                activityList.clear()
                activityList.addAll(filteredList)
                orderByDateDes()
                if (activityList.isEmpty()) {
                    _result.value = ListResult.NODATA
                } else {
                    _result.value = ListResult.SUCCESS
                }
            },
            {
                _result.value = ListResult.FAILURE
            },
            {
                _result.value = ListResult.NODATA
            }
        )
    }
    fun getUserActivities(
        list: String
    ) {
        _result.value = ListResult.LOADING
        val aux = ArrayList<Actividad>()
        ActivityRepository.getAllActivities(
            {
                aux.addAll(ActivityRepository.allActivities)
                val filteredList = aux.filter { activity ->
                    var userActivity = false
                    if (list == Constants.owned) {
                        if (activity.creator!!.id == currentUser!!.id) {
                            userActivity = true
                        }
                    } else {
                        if (activity.participantsIds.contains(currentUser!!.id)) {
                            userActivity = true
                        }
                    }
                  userActivity
                }
                activityList.clear()
                activityList.addAll(filteredList)
                orderByDateDes()
                if (activityList.isEmpty()) {
                    _result.value = ListResult.NODATA
                } else {
                    _result.value = ListResult.SUCCESS
                }
            },
            {
                _result.value = ListResult.FAILURE
            },
            {
                _result.value = ListResult.NODATA
            }
        )
    }


}
