package com.vaadin.demo.dashboard.domain

import java.io.Serializable
import java.util.Date

data class Transaction(var time: Date? = null,
    var country: String? = null,
    var city: String? = null,
    var theater: String? = null,
    var room: String? = null,
    var seats: Int = 0,
    var price: Double = 0.toDouble(),
    var movieId: Long = 0,
    var title: String? = null) : Serializable
