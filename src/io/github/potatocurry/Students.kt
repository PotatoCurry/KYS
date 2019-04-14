package io.github.potatocurry

import kotlin.collections.HashMap

object Students {
    private var students = HashMap<Int, Student>()

    fun initialize() {
        students.clear()
    }

    fun exists(id: Int): Boolean {
        return students.containsKey(id)
    }

    fun add(number: Int, student: Student) {
        students[number] = student
    }

    operator fun get(number: Int): Student? {
        return students[number]
    }
}
