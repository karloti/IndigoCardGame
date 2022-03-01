fun printColor(myImage: BufferedImage) = readln().split(' ').map { it.toInt() }
    .let { (x, y) -> myImage.getRGB(x, y) }
    .let { color -> listOf(color ushr 16 and 0xFF, color ushr 8 and 0xFF, color and 0xFF, color ushr 24) }
    .joinToString(" ")
    .let(::println)