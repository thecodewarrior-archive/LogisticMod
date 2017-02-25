package thecodewarrior.logistic.util.collections

import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.util.math.BlockPos

/**
 * Created by TheCodeWarrior
 */
class BlockMap<T: Any> {
    private val map: TLongObjectMap<T> = TLongObjectHashMap<T>()

    val keySet: Iterable<BlockPos> by lazy {
        object : Iterable<BlockPos> {
            override fun iterator(): Iterator<BlockPos> {
                val realIter = map.keySet().iterator()
                return object : Iterator<BlockPos> {
                    override fun hasNext() = realIter.hasNext()
                    override fun next() = BlockPos.fromLong(realIter.next())
                }
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
}
