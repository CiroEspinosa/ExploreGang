package com.example.exploregang.data.model

import java.util.*

class UserRegisterFields(
    var name: String?,
    var surname: String?,
    var nick: String?,
    var photo: String?,
    var dni: String?,
    var dob:Date?,
    var phone: String?,
    var email: String?,
    var password: String?,
    var passwordRepeat: String?,
    var isPublic:Boolean?,
  /*  var tutorName: String?,
    var tutorEmail: String?,
    var tutorPhone: String?,*/
) {
    constructor() : this(null, null,null,null,null, null,null,null,null,null,null)

}