package com.example.wavesoffood.Model

import java.io.Serializable

data class OrderInfo(
    val orderedBy: String? = null,
    val customerEMail: String? = null,
    var deliveryAddress: String? = null,
    var deliveryNumber: String? = null,
    var orderedOn: Long? = null,
    val billAmount: String? = null,
    val orderedItems: ArrayList<Dish>? = null,
    var isAccepted: Boolean = false,
    var acceptedOn: Long? = null,
    val isDelivered: Boolean = false,
    val isPaymentDone: Boolean = false,
): Serializable
