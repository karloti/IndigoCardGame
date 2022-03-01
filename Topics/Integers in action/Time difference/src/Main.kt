fun getSeconds() = List(3) { readln().toInt() }.fold(1) { total, t -> total * 60 + t }

fun main() = println(-getSeconds() + getSeconds())