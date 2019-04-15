package io.github.potatocurry

import kotlin.collections.HashMap

/** Holds students and acts as interface for adding/removing students from database. */
object Students {
    private var students = HashMap<Int, Student>()

    /** Clears students database. */
    fun clear() {
        students.clear()
    }

    /** Checks if the specified student by ID exists. */
    fun exists(id: Int): Boolean {
        return students.containsKey(id)
    }

    /** Adds a new student to the database. */
    fun add(number: Int, student: Student) {
        students[number] = student
    }

    /** Returns the specified student by ID. */
    operator fun get(number: Int): Student? {
        return students[number]
    }
}
