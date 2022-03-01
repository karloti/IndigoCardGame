import java.util.Scanner

fun main(args: Array<String>) {
    when (1) {
        1 -> Scanner(System.`in`).nextInt().let() { println(it % 10 + it / 10 % 10 + it / 100) } // in this case
        2 -> Scanner(System.`in`).next().sumBy { Character.getNumericValue(it) }.let { println(it) } // universal 1
        3 -> println(readLine()!!.map { it.toString() }.map { it.toInt() }.sum()) // universal 2
    }
}
