package io.github.potatocurry

/** Holds students and acts as interface for adding/removing students from database. */
object Students {
    private var students = HashMap<Int, Student>()

    /** Clears students database. */
    fun clear() {
        students.clear()
    }

    /** Adds a new student to the database. */
    fun add(number: Int, student: Student): Student {
        students[number] = student
        return student
    }

    /** Returns the specified student by ID. */
    operator fun get(number: Int): Student? {
        return students[number]
    }

    /** Returns a random student number. */
    fun getRandomNumber(): Int {
        return students.keys.random()
    }
}
