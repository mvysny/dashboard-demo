package com.vaadin.demo.dashboard.domain

import java.io.Serializable

data class DashboardNotification(var id: Long = 0,
    var content: String? = null,
    var isRead: Boolean = false,
    var firstName: String? = null,
    var lastName: String? = null,
    var prettyTime: String? = null,
    var action: String? = null) : Serializable
