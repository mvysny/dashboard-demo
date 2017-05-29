package com.vaadin.demo.dashboard.domain

import java.io.Serializable
import java.util.Date

data class MovieRevenue(var timestamp: Date? = null, var title: String? = null, var revenue: Double? = null): Serializable
