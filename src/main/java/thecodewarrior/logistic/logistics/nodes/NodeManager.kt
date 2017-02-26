package thecodewarrior.logistic.logistics.nodes

import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import thecodewarrior.logistic.LogisticLog
import thecodewarrior.logistic.logistics.NodeLogisticData
import thecodewarrior.logistic.logistics.WorldCapLogistic
import thecodewarrior.logistic.util.collections.BlockMap

/**
 * Created by TheCodeWarrior
 */
@SaveInPlace
class NodeManager(val cap: WorldCapLogistic) {
    /*==================================================================================================================
                    API
    ==================================================================================================================*/
    fun getNode(pos: BlockPos): Node? = nodes.get(pos)

    fun addNode(pos: BlockPos, offset: Vec3d, logistic: NodeLogisticData?): Boolean {
        var offset = offset
        if(offset.xCoord < -3 || offset.yCoord < -3 || offset.zCoord < -3 || offset.xCoord > 3 || offset.yCoord > 3 || offset.zCoord > 3) {
            LogisticLog.warn("Node offset to large (%s), it must be inside [±3, ±3, ±3]", offset)
            offset = Vec3d(Math.min(3.0, Math.max(-3.0, offset.xCoord)), Math.min(3.0, Math.max(-3.0, offset.yCoord)), Math.min(3.0, Math.max(-3.0, offset.zCoord)))
        }
        if(nodes.contains(pos)) {
            LogisticLog.error("Can not add node to %s for pos %s, the block already has a node.", cap.world, pos)
            return false
        }
        val node = Node(pos, offset, logistic)
        nodes[pos] = node
        cap.changeTracker.addNode(node)
        return true
    }

    fun removeNode(pos: BlockPos): Boolean {
        if(nodes.contains(pos)) {
            LogisticLog.error("Can not add node to %s for pos %s, the block already has a node.", cap.world, pos)
            return false
        }
        nodes.remove(pos)
        cap.changeTracker.removeNode(pos)
        return true
    }

    /*==================================================================================================================
                    IMPLEMENTATION
    ==================================================================================================================*/
    @Save
    private val nodes = BlockMap<Node>()
}
