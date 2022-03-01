fun main() = IntArray(3) { readLine()!!.toInt() }.map { (it + 1) / 2 }.sumOf { it }.let(::println)
