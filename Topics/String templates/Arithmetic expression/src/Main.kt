import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val (a, b) = Array(2) { scanner.nextInt() }
    println("$a plus $b equals ${a + b}")
}
