package pirarucu.util

class FixedSizeMap<T, V>(size: Int) {

    private val list = mutableListOf<T>()
    private val map = mutableMapOf<T, V>()

    var storedElements = 0
    var size = size

    fun add(element: T, value: V) {
        var count = list.size
        if (contains(element)) {
            list.remove(element)
            map.remove(element)
            count--
        } else if (count == size) {
            val removedElement = list.removeAt(0)
            map.remove(removedElement)
            count--
        } else {
            storedElements++
        }

        list.add(count, element)
        map[element] = value
    }

    fun contains(item: T): Boolean {
        return map.containsKey(item)
    }

    fun getValue(item: T): V? {
        return map[item]
    }
}