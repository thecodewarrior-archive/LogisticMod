package thecodewarrior.logistic.util.collections

import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.util.math.BlockPos

/**
 * Created by TheCodeWarrior
 */
@SaveInPlace
class BlockMap<T: Any> {
    @Save
    private val map: TLongObjectMap<T> = TLongObjectHashMap<T>()

    val values: Collection<T>
        get() = map.valueCollection()

    val keys: Set<BlockPos> = object: Set<BlockPos> {
        override val size: Int
            get() = map.size()

        override fun contains(element: BlockPos) = map.containsKey(element.toLong())

        override fun containsAll(elements: Collection<BlockPos>) = elements.all { this.contains(it) }

        override fun isEmpty() = map.isEmpty

        override fun iterator(): Iterator<BlockPos> {
            val backingIter = map.keySet().iterator()
            return object : Iterator<BlockPos> {
                override fun hasNext() = backingIter.hasNext()

                override fun next() = BlockPos.fromLong(backingIter.next())

            }
        }

    }

    operator fun get(key: BlockPos): T? {
        return map.get(key.toLong())
    }

    operator fun set(key: BlockPos, value: T) {
        map.put(key.toLong(), value)
    }

    operator fun contains(key: BlockPos): Boolean {
        return map.containsKey(key.toLong())
    }

    fun remove(key: BlockPos): T? {
        return map.remove(key.toLong())
    }

    fun clear() {
        map.clear()
    }
}
