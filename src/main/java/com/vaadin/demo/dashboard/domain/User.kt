package com.vaadin.demo.dashboard.domain

import java.io.Serializable

data class User(var role: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var title: String? = null,
    var isMale: Boolean = false,
    var email: String? = null,
    var location: String? = null,
    var phone: String? = null,
    var newsletterSubscription: Int? = null,
    var website: String? = null,
    var bio: String? = null) : Serializable

