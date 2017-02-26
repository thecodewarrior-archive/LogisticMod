package thecodewarrior.logistic.util.collections

import com.teamwizardry.librarianlib.common.util.saving.Savable
import com.teamwizardry.librarianlib.common.util.saving.Save
import gnu.trove.map.TLongFloatMap
import gnu.trove.map.hash.TLongFloatHashMap
import net.minecraft.util.math.BlockPos

/**
 * Created by TheCodeWarrior
 */
@Savable
class BlockFloatMap {
    @Save
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
