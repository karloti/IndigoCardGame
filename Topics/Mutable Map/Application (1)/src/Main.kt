fun main() {
    val studentsMarks = mutableMapOf<String, Int>()
    while (true) studentsMarks.putIfAbsent(readln().takeIf { it != "stop" } ?: break, readln().toInt())
    println(studentsMarks)
}