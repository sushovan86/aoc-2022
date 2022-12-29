class DistressSignal private constructor() {

    data class Packet(
        var integer: Int? = null,
        val list: MutableList<Packet> = mutableListOf()
    ) : Comparable<Packet> {

        private val isInteger: Boolean
            get() = integer != null

        private fun asList() = if (this.isInteger) listOf(Packet(integer)) else list

        override infix operator fun compareTo(other: Packet): Int {

            if (isInteger && other.isInteger) {
                return integer!! - other.integer!!
            } else {

                val firstItemList = asList()
                val secondItemList = other.asList()

                for (index in 0 until minOf(firstItemList.size, secondItemList.size)) {

                    val item1: Packet = firstItemList[index]
                    val item2: Packet = secondItemList[index]

                    val comparison = item1 compareTo item2
                    if (comparison != 0) {
                        return comparison
                    }
                }
                return firstItemList.size - secondItemList.size
            }
        }

        override fun equals(other: Any?): Boolean = (other as? Packet)?.let {
            this compareTo it == 0
        } ?: false

        override fun hashCode(): Int {
            var result = integer ?: 0
            result = 31 * result + list.hashCode()
            return result
        }

        override fun toString(): String = if (isInteger) integer.toString() else list.toString()
    }

    private val packetPairList = mutableListOf<Pair<Packet, Packet>>()

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

        private fun extractPacket(packetString: String): Packet {

            val packetStack = ArrayDeque<Packet>()
            packetStack += Packet()

            var probableIntegerStartIndex = 0

            for (index in packetString.indices) {

                if (packetString[index] == '[') {
                    val newPacket = Packet()
                    packetStack.first().list += newPacket
                    packetStack.addFirst(newPacket)

                    probableIntegerStartIndex = index

                } else if (packetString[index] == ']') {

                    addToList(packetString, probableIntegerStartIndex, index, packetStack)
                    packetStack.removeFirst()

                } else if (packetString[index] == ',') {

                    addToList(packetString, probableIntegerStartIndex, index, packetStack)
                    probableIntegerStartIndex = index
                }
            }
            return packetStack.first().list.first()
        }

        private fun addToList(
            trimmedString: String,
            probableIntegerStartIndex: Int,
            index: Int,
            packetStack: ArrayDeque<Packet>
        ) {

            if (index - probableIntegerStartIndex > 1) {

                val integer = trimmedString.subSequence(probableIntegerStartIndex + 1, index)
                    .toString().trim().toIntOrNull()
                if (integer != null) {
                    packetStack.first().list += Packet(integer = integer)
                }
            }
        }
    }

    fun getSumOfIndicesOfCorrectPairs(): Int = packetPairList
        .withIndex()
        .filter {
            val (first, second) = it.value
            first < second
        }
        .sumOf { it.index + 1 }

    fun getDecoderKeyOfSignal(): Int {

        val dividerPacket1 = Packet(list = mutableListOf(Packet(list = mutableListOf(Packet(2)))))
        val dividerPacket2 = Packet(list = mutableListOf(Packet(list = mutableListOf(Packet(6)))))

        val listWithDividerPackets = mutableListOf(*packetPairList.toTypedArray()) +
                (dividerPacket1 to dividerPacket2)

        return listWithDividerPackets
            .flatMap { it.toList() }
            .asSequence()
            .sorted()
            .withIndex()
            .filter { it.value == dividerPacket1 || it.value == dividerPacket2 }
            .map { it.index + 1 }
            .reduce { acc, index -> acc * index }
    }
}

fun main() {

    val testInput = readInputAsText("Day13_test")
    val testDistressSignal = DistressSignal.load(testInput)
    check(testDistressSignal.getSumOfIndicesOfCorrectPairs() == 13)
    check(testDistressSignal.getDecoderKeyOfSignal() == 140)

    val actualInput = readInputAsText("Day13")
    val actualDistressSignal = DistressSignal.load(actualInput)
    println(actualDistressSignal.getSumOfIndicesOfCorrectPairs())
    println(actualDistressSignal.getDecoderKeyOfSignal())
}