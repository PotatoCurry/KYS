package io.github.potatocurry

/** Holds student information. */
class Student(val firstName: String, val lastName: String, val gradClass: Int) {
    val activities = mutableListOf<VolunteerActivity>()

    val totalHours: Double
        get() = activities.sumByDouble(VolunteerActivity::hours)

    val totalExtraHours: Double
        get() = activities.sumByDouble(VolunteerActivity::extraHours)

    /** Enters volunteer activity into the student's records. */
    fun enterActivity(va: VolunteerActivity): VolunteerActivity {
        activities.add(va)
        return va
    }
}
