package com.example.wavesoffood.Model


data class CustomerUser(
    var userId: String? = null,
    var userName: String? = null,
    var phone: String? = null,
    var eMail: String? = null,
    var password: String? = null,
    var address: String? = null,
    var userImage: String? = null,
    var userType: String? = null,
    var signInMethod: String? = null
){
//    constructor(userId: String, userName: String, eMail: String, password: String, userType: String, signInMethod: String?)
//            : this(userId, userName,"", eMail, password, "","", userType, signInMethod)
}
