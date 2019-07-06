package io.github.potatocurry.kys

/** Holds [Student] objects and acts as interface for accessing database. */
object Students {
    private var students = HashMap<Int, Student>()

    /** Clears database. */
    fun clear() {
        students.clear()
    }

    /** Adds a new [student] with [number] to the database. */
    fun add(number: Int, student: Student): Student {
        students[number] = student
        return student
    }

    /** Returns the specified [Student] by [number]. */
    operator fun get(number: Int): Student? {
        return students[number]
    }

    /** Returns a random [Student] number. */
    fun getRandom(): Int {
        return students.keys.random()
    }
}
