class DistressSignal private constructor() {

    data class PacketItem(
        var integer: Int? = null,
        val list: MutableList<PacketItem> = mutableListOf()
    ) {

        private val isInteger: Boolean
            get() = integer != null

        private fun asList() = if (this.isInteger) listOf(PacketItem(integer)) else list

        operator fun minus(other: PacketItem): Int {

            if (isInteger && other.isInteger) {
                return integer!! - other.integer!!
            } else {

                val firstItemList = asList()
                val secondItemList = other.asList()

                for (index in 0 until minOf(firstItemList.size, secondItemList.size)) {

                    val item1: PacketItem = firstItemList[index]
                    val item2: PacketItem = secondItemList[index]

                    val diff = item1 - item2
                    if (diff != 0) {
                        return diff
                    }
                }
                return firstItemList.size - secondItemList.size
            }
        }

        operator fun compareTo(other: PacketItem) = this - other

        override fun toString(): String = if (isInteger) integer.toString() else list.toString()
    }

    private val packetPairList = mutableListOf<Pair<PacketItem, PacketItem>>()

    companion object {

        private val PACKET_SEPARATOR = System.lineSeparator() + System.lineSeparator()

        fun load(input: String): DistressSignal {

            val distressSignal = DistressSignal()
            for (packetPairString in input.split(PACKET_SEPARATOR)) {
                val (packet1String, packet2String) = packetPairString.lines()
                val packets = extractPacket(packet1String) to extractPacket(packet2String)

                distressSignal.packetPairList += packets
            }
            return distressSignal
        }

        private fun extractPacket(packetString: String): PacketItem {

            val packetItemStack = ArrayDeque<PacketItem>()
            packetItemStack += PacketItem()

            var probableIntegerStartIndex = 0

            for (index in packetString.indices) {

                if (packetString[index] == '[') {
                    val newPacketItem = PacketItem()
                    packetItemStack.first().list += newPacketItem
                    packetItemStack.addFirst(newPacketItem)

                    probableIntegerStartIndex = index

                } else if (packetString[index] == ']') {

                    addToList(packetString, probableIntegerStartIndex, index, packetItemStack)
                    packetItemStack.removeFirst()

                } else if (packetString[index] == ',') {

                    addToList(packetString, probableIntegerStartIndex, index, packetItemStack)
                    probableIntegerStartIndex = index
                }
            }
            return packetItemStack.first().list.first()
        }

        private fun addToList(
            trimmedString: String,
            probableIntegerStartIndex: Int,
            index: Int,
            packetItemStack: ArrayDeque<PacketItem>
        ) {

            if (index - probableIntegerStartIndex > 1) {

                val integer = trimmedString.subSequence(probableIntegerStartIndex + 1, index)
                    .toString().trim().toIntOrNull()
                if (integer != null) {
                    packetItemStack.first().list += PacketItem(integer = integer)
                }
            }
        }
    }

    fun correctPacketIndexSum() = packetPairList
        .withIndex()
        .filter {
            val (first, second) = it.value
            first < second
        }
        .sumOf { it.index + 1 }

}

fun main() {

    val testInput = readInputAsText("Day13_test")
    val testDistressSignal = DistressSignal.load(testInput)
    check(testDistressSignal.correctPacketIndexSum() == 13)

    val actualInput = readInputAsText("Day13")
    val actualDistressSignal = DistressSignal.load(actualInput)
    println(actualDistressSignal.correctPacketIndexSum())
}