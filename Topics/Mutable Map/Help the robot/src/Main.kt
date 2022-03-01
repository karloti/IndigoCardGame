fun helpingTheRobot(purchases: Map<String, Int>, addition: Map<String, Int>): Map<String, Int> = buildMap {
    putAll(purchases)
    for ((key, value) in addition) merge(key, value, Int::plus)
}