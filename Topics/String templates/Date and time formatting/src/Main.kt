import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    println(Array(6) { scanner.nextInt() }.let { "${it[0]}:${it[1]}:${it[2]} ${it[3]}/${it[4]}/${it[5]}" })
}