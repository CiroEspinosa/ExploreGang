package com.example.exploregang.data.model

data class User(
    var name:String,
    var email: String,
    var password: String,
    var surname:String,
    var nick:String,
    var personalId:String?,
    var dob:String?,
    var phone:String?,
    var image: String?
)