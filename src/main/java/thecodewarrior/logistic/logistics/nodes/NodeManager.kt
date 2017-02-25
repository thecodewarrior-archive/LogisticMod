package thecodewarrior.logistic.logistics.nodes

import com.teamwizardry.librarianlib.common.util.saving.Savable
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import thecodewarrior.logistic.LogisticLog
import thecodewarrior.logistic.logistics.NodeType
import thecodewarrior.logistic.logistics.WorldCapLogistic
import thecodewarrior.logistic.util.collections.BlockMap

/**
 * Created by TheCodeWarrior
 */
@Savable
class NodeManager(val cap: WorldCapLogistic) {
    /*==================================================================================================================
                    API
    ==================================================================================================================*/
    fun getNode(pos: BlockPos): Node? = nodes.get(pos)

    fun addNode(pos: BlockPos, offset: Vec3d, type: NodeType): Boolean {
        var offset = offset
        if(offset.xCoord < -3 || offset.yCoord < -3 || offset.zCoord < -3 || offset.xCoord > 3 || offset.yCoord > 3 || offset.zCoord > 3) {
            LogisticLog.warn("Node offset to large (%s), it must be inside [±3, ±3, ±3]", offset)
            offset = Vec3d(Math.min(3.0, Math.max(-3.0, offset.xCoord)), Math.min(3.0, Math.max(-3.0, offset.yCoord)), Math.min(3.0, Math.max(-3.0, offset.zCoord)))
        }
        if(nodes.contains(pos)) {
            LogisticLog.error("Can not add node to %s for pos %s, the block already has a node.", cap.world, pos)
            return false
        }
        nodes[pos] = Node(pos, offset, type)
        return true
    }

    fun removeNode(pos: BlockPos): Boolean {
        if(nodes.contains(pos)) {
            LogisticLog.error("Can not add node to %s for pos %s, the block already has a node.", cap.world, pos)
            return false
        }
        nodes.remove(pos)
        return true
    }

    /*==================================================================================================================
                    IMPLEMENTATION
    ==================================================================================================================*/
    @Save
    private val nodes = BlockMap<Node>()
}
