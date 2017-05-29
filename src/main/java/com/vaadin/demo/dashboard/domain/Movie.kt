package com.vaadin.demo.dashboard.domain

import java.io.Serializable
import java.util.Date

data class Movie(var id: Long = 0,
    var title: String? = null,
    var synopsis: String? = null,
    var thumbUrl: String? = null,
    var posterUrl: String? = null,
    var duration: Int = 0,
    var releaseDate: Date? = null,
    var score: Int = 0) : Serializable
