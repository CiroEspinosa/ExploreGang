package com.example.exploregang.ui.userList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.data.model.User
import com.example.exploregang.data.repository.UserRepository
import com.example.exploregang.data.repository.UserRepository.currentUser
import com.example.exploregang.ui.activityList.ListResult

class UserListViewModel : ViewModel() {
    var userList = mutableListOf<User>()

    private val _result = MutableLiveData<ListResult>()
    val result: LiveData<ListResult>
        get() = _result

    fun getAllUsers(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = { },
        onNoData: () -> Unit = { }
    ) {
        _result.value = ListResult.LOADING
        UserRepository.getAllUsers({
            userList.clear()
            userList.addAll(UserRepository.allUsers)
            _result.value = ListResult.SUCCESS
            onSuccess()
        }, {
            _result.value = ListResult.FAILURE
            onError()
        }, {
            _result.value = ListResult.NODATA
            onNoData()
        })
    }

    fun filterUsers(name: String) {
        _result.value = ListResult.LOADING
        val aux = ArrayList<User>()
        UserRepository.getAllUsers({
            aux.addAll(UserRepository.allUsers)
            val filteredList = aux.filter { user -> user.name!!.contains(name,true)||user.email!!.contains(name) }
            userList.clear()
            userList.addAll(filteredList)
            if(userList.isEmpty()){
                _result.value=ListResult.NODATA
            }else{
                _result.value = ListResult.SUCCESS
            }

        }, {
            _result.value = ListResult.FAILURE
        }, {
                _result.value = ListResult.NODATA
            })
    }

    fun getEnrolledUsers(activity: Actividad) {
        _result.value = ListResult.LOADING
        val aux = ArrayList<User>()
        UserRepository.getAllUsers({
            aux.addAll(UserRepository.allUsers)
            val filteredList = aux.filter { user ->
                user.enrolledActivities!!.contains(activity.id) }
            userList.clear()
            userList.addAll(filteredList)
            if(userList.isEmpty()){
                _result.value=ListResult.NODATA
            }else{
                _result.value = ListResult.SUCCESS
            }

        }, {
            _result.value = ListResult.FAILURE
        }, {
            _result.value = ListResult.NODATA
        })
    }

    fun filterEnrolledUsers(name: String,activity:Actividad) {
        _result.value = ListResult.LOADING
        val aux = ArrayList<User>()
        UserRepository.getAllUsers({
            aux.addAll(UserRepository.allUsers)
            val filteredList = aux.filter { user -> user.name!!.contains(name,true)&& user.enrolledActivities!!.contains(activity.id) }
            userList.clear()
            userList.addAll(filteredList)
            if(userList.isEmpty()){
                _result.value=ListResult.NODATA
            }else{
                _result.value = ListResult.SUCCESS
            }

        }, {
            _result.value = ListResult.FAILURE
        }, {
            _result.value = ListResult.NODATA
        })
    }

    fun getContacts() {
        _result.value = ListResult.LOADING
        val aux = ArrayList<User>()
        UserRepository.getAllUsers({
            aux.addAll(UserRepository.allUsers)
            val filteredList = aux.filter { user -> UserRepository.currentUser!!.contacts!!.contains(user.id) }
            userList.clear()
            userList.addAll(filteredList)
            if(userList.isEmpty()){
                _result.value=ListResult.NODATA
            }else{
                _result.value = ListResult.SUCCESS
            }

        }, {
            _result.value = ListResult.FAILURE
        }, {
            _result.value = ListResult.NODATA
        })
    }

    fun filterContacts(name: String) {
        _result.value = ListResult.LOADING
        val aux = ArrayList<User>()
        UserRepository.getAllUsers({
            aux.addAll(UserRepository.allUsers)
            val filteredList = aux.filter { user -> UserRepository.currentUser!!.contacts!!.contains(user.id) && currentUser!!.name!!.contains(name,true)}
            userList.clear()
            userList.addAll(filteredList)
            if(userList.isEmpty()){
                _result.value=ListResult.NODATA
            }else{
                _result.value = ListResult.SUCCESS
            }

        }, {
            _result.value = ListResult.FAILURE
        }, {
            _result.value = ListResult.NODATA
        })
    }
}