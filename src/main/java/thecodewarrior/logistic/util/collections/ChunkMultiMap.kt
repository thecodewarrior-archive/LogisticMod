package thecodewarrior.logistic.util.collections

import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.util.math.ChunkPos

/**
 * Created by TheCodeWarrior
 */

@SaveInPlace
class ChunkMultiMap<T: Any> {
    @Save
    private val map: TLongObjectMap<MutableSet<T>> = TLongObjectHashMap<MutableSet<T>>()

    val values: Collection<Collection<T>>
        get() = map.valueCollection()

    val keys: Set<ChunkPos> = object: Set<ChunkPos> {
        override val size: Int
            get() = map.size()

        override fun contains(element: ChunkPos) = map.containsKey(toLong(element))

        override fun containsAll(elements: Collection<ChunkPos>) = elements.all { this.contains(it) }

        override fun isEmpty() = map.isEmpty

        override fun iterator(): Iterator<ChunkPos> {
            val backingIter = map.keySet().iterator()
            return object : Iterator<ChunkPos> {
                override fun hasNext() = backingIter.hasNext()

                override fun next() = fromLong(backingIter.next())

            }
        }

    }

    operator fun get(key: ChunkPos): Collection<T> {
        return map.get(toLong(key)) ?: listOf()
    }

    fun add(key: ChunkPos, value: T) {
        val k = toLong(key)
        if(!map.containsKey(k))
            map.put(k, mutableSetOf())
        map.get(k)!!.add(value)
    }

    operator fun contains(key: ChunkPos): Boolean {
        return map.containsKey(toLong(key))
    }

    fun remove(key: ChunkPos, value: T) {
        val k = toLong(key)
        if(map.containsKey(k)) {
            val set = map.get(k)
            if(set != null) {
                set.remove(value)
                if (set.size == 0)
                    map.remove(k)
            }
        }
    }

    fun clear() {
        map.clear()
    }

    private fun toLong(chunk: ChunkPos): Long {
        return (chunk.chunkXPos.toLong() shl 32) or (chunk.chunkZPos.toLong() and 0x00000000ffffffffL)
    }
    private fun fromLong(l: Long): ChunkPos {
        val x = (l shr 32).toInt()
        val z = l.toInt()
        return ChunkPos(x, z)
    }

}
