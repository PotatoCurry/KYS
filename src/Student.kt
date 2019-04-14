class Student(val firstName: String, val lastName: String, val gradClass: Int) {
    val activities: MutableList<VolunteerActivity> = mutableListOf()

    val totalHours: Double
        get() {
            var hours = 0.0
            for (va in activities)
                hours += va.hours
            return hours
        }

    fun enterActivity(va: VolunteerActivity) {
        activities.add(va)
    }
}
