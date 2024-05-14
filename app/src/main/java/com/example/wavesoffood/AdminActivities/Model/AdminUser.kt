package com.example.wavesoffood.AdminActivities.Model

data class AdminUser(
    var userId: String? = null,
    var ownersName : String? = null,
    var resName : String? = null,
    var resAddress: String? = null,
    var resNumber: String? = null,
    var eMail : String? = null,
    var password : String? = null,
    var adminImage : String? = null,
    var userType: String? = null,
    var signInMethod: String? = null
)
