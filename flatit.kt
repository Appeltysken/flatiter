class FlatIterator(private val inner_: Iterator<IntOrIterInt>) : Iterator<Int> {
    private var currentIterator: Iterator<Int> = listOf<Int>().iterator()
    private var currentElement: Int? = 0
    private var currentElementProcessed: Boolean = true
    private var endReached: Boolean = false
    private var lastElementEmpty: Boolean = false

    override fun hasNext(): Boolean {
        return !endReached || currentIterator.hasNext() || inner_.hasNext() && !lastElementEmpty
    }

    override fun next(): Int {
        if (lastElementEmpty) {
            throw NoSuchElementException()
        }
        while (true) {
            if (currentIterator.hasNext()) {
                return currentIterator.next()
            } else if (inner_.hasNext()) {
                val nextElement = inner_.next()
                if (nextElement is Left) {
                    currentElement = nextElement.l
                    currentElementProcessed = false
                } else if (nextElement is Right) {
                    currentIterator = nextElement.r
                    currentElementProcessed = true
                    currentIterator = currentIterator.takeIf { it.hasNext() } ?: listOf<Int>().iterator()
                    endReached = !inner_.hasNext() && !currentIterator.hasNext()
                    lastElementEmpty = !currentIterator.hasNext()
                }
                if (!currentElementProcessed) {
                    currentElementProcessed = true
                    return currentElement!!
                }
            } else {
                endReached = true
                lastElementEmpty = true
                return currentElement!!
            }
        }
    }
}

fun main() {
    val inner_: Iterator<IntOrIterInt> = listOf(
        Left(1),
        Left(2),
        Left(3),
        Right(listOf(4,5,6).iterator()),
        Right(listOf<Int>().iterator()),
        Left(7),
        Right(listOf<Int>().iterator())
    ).iterator()
    val flatIterator = FlatIterator(inner_)

    while (flatIterator.hasNext()) {
        print("${flatIterator.next()} ") // Output: 1 2 3 4 5 6 7
