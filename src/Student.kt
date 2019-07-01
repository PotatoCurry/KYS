package io.github.potatocurry.kys

/** Holds student information. */
class Student(val firstName: String, val lastName: String, val gradClass: Int) {
    private val activities = mutableListOf<VolunteerActivity>()
    val records: List<VolunteerActivity> = activities

    val totalHours: Double
        get() = activities.sumByDouble(VolunteerActivity::hours)

    val totalExtraHours: Double
        get() = activities.sumByDouble(VolunteerActivity::extraHours)

    /** Enters [va] into the [Student] records. */
    fun enterActivity(va: VolunteerActivity): VolunteerActivity {
        activities.add(va)
        return va
    }
}

/** Holds volunteer activity information. */
data class VolunteerActivity(
    val agency: String,
    val startDate: String,
    val endDate: String,
    val hours: Double,
    val extraHours: Double,
    val isSummer: Boolean,
    val description: String
)
