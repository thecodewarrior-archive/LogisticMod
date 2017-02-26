package thecodewarrior.logistic.logistics.nodes

import com.teamwizardry.librarianlib.common.util.saving.Savable
import com.teamwizardry.librarianlib.common.util.saving.SavableConstructorOrder
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import thecodewarrior.logistic.logistics.NodeLogisticData
import thecodewarrior.logistic.util.collections.BlockFloatMap

/**
 * Created by TheCodeWarrior
 */
@Savable
class Node
@SavableConstructorOrder("pos", "offset", "logistic", "connections") private constructor(
        @Save val pos: BlockPos,
        @Save val offset: Vec3d,
        @Save val logistic: NodeLogisticData?,
        @Save private val connections: BlockFloatMap
) {
    constructor(pos: BlockPos, offset: Vec3d, logi: NodeLogisticData?) : this(pos, offset, logi, BlockFloatMap())
}
