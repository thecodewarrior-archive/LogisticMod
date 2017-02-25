package thecodewarrior.logistic.util.collections

import gnu.trove.map.TLongFloatMap
import gnu.trove.map.hash.TLongFloatHashMap
import net.minecraft.util.math.BlockPos

/**
 * Created by TheCodeWarrior
 */
class BlockFloatMap {
    private val map: TLongFloatMap = TLongFloatHashMap()

    operator fun get(key: BlockPos): Float {
        return map.get(key.toLong())
    }

    operator fun set(key: BlockPos, value: Float) {
        map.put(key.toLong(), value)
    }

    operator fun contains(key: BlockPos): Boolean {
        return map.containsKey(key.toLong())
    }

    fun remove(key: BlockPos): Float {
        return map.remove(key.toLong())
    }
}
