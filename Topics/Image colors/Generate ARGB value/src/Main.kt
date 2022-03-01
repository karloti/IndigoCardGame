/**
 * I know that it is not very effective, as calculations can be stopped much earlier in
 * case of incorrect data entry, but it is still functional programming, and I like it that way.
 */

fun printARGB() = readln()
    .split(' ', limit = 4)
    .mapIndexedNotNull { i, s -> s.toUByteOrNull()?.toUInt()?.shl((3 - i) * 8) }
    .run { if (size == 4) sum() else "Invalid input" }
    .let(::println)