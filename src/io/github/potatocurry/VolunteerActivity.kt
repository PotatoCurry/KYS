package io.github.potatocurry

/** Holds volunteer activity information. */
class VolunteerActivity(
    val agency: String,
    val startDate: String,
    val endDate: String,
    val hours: Double,
    val isSummer: Boolean,
    val description: String
)
