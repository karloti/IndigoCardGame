open class Choice(val text: String) {
    private class Success : Choice("Success " + (1..100).random())
    private class Failed : Choice("Failed")
    companion object {
        val SUCCESS: Choice = Success()
        val FAILED: Choice = Failed()
    }
}

fun main() {
    println(Choice.SUCCESS)
    println(Choice::class.simpleName)
}