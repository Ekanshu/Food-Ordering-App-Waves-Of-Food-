package com.example.wavesoffood.Model

import java.io.Serializable

data class Dish (
    val dishImage: String? = null,
    val dishName: String? = null,
    val dishPrice: String? = null,
    var dishQuantity: Int? = null
): Serializable