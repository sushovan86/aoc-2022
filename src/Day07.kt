import java.util.*

sealed class Node(
    val name: String,
    val parent: Directory? = null,
    protected var size: Int = 0
) {

    fun getNodeSize() = size

    override fun equals(other: Any?): Boolean {
        return Objects.equals(this, other)
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }
}

class Directory(
    name: String,
    parent: Directory? = null
) : Node(name, parent) {

    private val children: LinkedHashSet<Node> = LinkedHashSet()

    fun addDirectory(name: String) {
        val newDirectory = Directory(name = name, parent = this)
        this.children += newDirectory
    }

    fun addFile(name: String, fileSize: Int) {

        val file = File(name = name, parent = this, size = fileSize)
        val isAdded = this.children.add(file)
        if (!isAdded) {
            return
        }

        // Adjust size of parent directories
        this.size += fileSize
        var parentDirectory = this.parent
        while (parentDirectory != null) {
            parentDirectory.size += fileSize
            parentDirectory = parentDirectory.parent
        }
    }

    fun goToDirectory(name: String): Directory =
        children.firstOrNull { it.name == name } as? Directory
            ?: error("Directory $name doesn't exist under ${this.name}")

    fun getAllSubDirectoriesUnderSize(maxSize: Int): List<Directory> {

        val directoryList = children.filterIsInstance<Directory>()
            .flatMap { it.getAllSubDirectoriesUnderSize(maxSize) }

        return if (size <= maxSize) {
            directoryList + this
        } else {
            directoryList
        }
    }
}

class File(
    name: String,
    parent: Directory,
    size: Int
) : Node(name, parent, size)

fun main() {

    fun Directory.getSumOfDirectoriesUnder(maxSize: Int) = getAllSubDirectoriesUnderSize(maxSize)
        .sumOf(Directory::getNodeSize)

    fun Directory.getDirectoryToDeleteToFreeUp(freeUpSpace: Int) = getAllSubDirectoriesUnderSize(Int.MAX_VALUE)
        .filter { it.getNodeSize() > freeUpSpace }
        .minOf(Directory::getNodeSize)

    fun calculateSpaceToFreeUp(rootTest: Directory): Int {

        val totalSpace = 70_000_000
        val totalUsedSpace = rootTest.getNodeSize()
        val availableFreeSpace = totalSpace - totalUsedSpace

        val spaceNeeded = 30_000_000
        return spaceNeeded - availableFreeSpace
    }

    fun parseDirectoryListing(line: String, directory: Directory) =
        when {
            line.startsWith("dir") -> directory
                .addDirectory(
                    line
                        .removePrefix("dir ")
                        .trim()
                )

            line.first().isDigit() -> {
                val (size, name) = line.split(" ")
                directory.addFile(name, size.toInt())
            }

            else -> error("Invalid Input $line")
        }


    fun parseDay07Input(input: String): Directory {

        val inputLines = input.split("$")
            .drop(2)
            .map(String::trim)

//        println(inputLines)

        val root = Directory("/")

        var current: Directory = root
        for (command in inputLines) {

            when (command.substring(0, 2)) {
                "cd" -> {
                    val commandParam = command.removePrefix("cd").trim()
                    current = when (commandParam) {
                        ".." -> current.parent ?: error("No Parent Directory to ${current.name}")
                        else -> current.goToDirectory(commandParam)
                    }
                }

                "ls" -> command.lines()
                    .drop(1)
                    .forEach { parseDirectoryListing(it, current) }

                else -> error("Invalid command $command")
            }
        }
        return root
    }

    val testInput = readInputAsText("Day07_test")
    val rootTest = parseDay07Input(testInput)

    check(rootTest.getSumOfDirectoriesUnder(100_000) == 95437)
    check(rootTest.getDirectoryToDeleteToFreeUp(calculateSpaceToFreeUp(rootTest)) == 24_933_642)

    val input = readInputAsText("Day07")
    val root = parseDay07Input(input)
    println(root.getSumOfDirectoriesUnder(100_000))
    println(root.getDirectoryToDeleteToFreeUp(calculateSpaceToFreeUp(root)))
}


