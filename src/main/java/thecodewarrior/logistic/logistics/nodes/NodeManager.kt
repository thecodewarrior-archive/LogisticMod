package thecodewarrior.logistic.logistics.nodes

import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import thecodewarrior.logistic.LogisticLog
import thecodewarrior.logistic.logistics.NodeLogisticData
import thecodewarrior.logistic.logistics.WorldCapLogistic
import thecodewarrior.logistic.util.collections.BlockMap
import thecodewarrior.logistic.util.collections.ChunkMultiMap

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
        checkChangeLimit()
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
        nodesByChunk.add(ChunkPos(pos), node)
        cap.changeTracker.addNode(node)
        return true
    }

    fun removeNode(pos: BlockPos): Boolean {
        checkChangeLimit()
        if(!nodes.contains(pos)) {
            LogisticLog.error("Can not remove node from %s for pos %s, the block does not have a node.", cap.world, pos)
            return false
        }
        val removed = nodes.remove(pos)
        if(removed != null) {
            nodesByChunk.remove(ChunkPos(pos), removed)
            cap.changeTracker.removeNode(pos)
        }
        return true
    }

    /*==================================================================================================================
                    IMPLEMENTATION
    ==================================================================================================================*/
    @Save
    private val nodes = BlockMap<Node>()

    @Save
    private val nodesByChunk = ChunkMultiMap<Node>()

    fun getNodesInChunk(chunk: ChunkPos): Collection<Node> {
        return nodesByChunk.get(chunk)
    }

    fun checkChangeLimit() {
        if(perTickChangeCount > MAX_CHANGES_PER_TICK)
            throw IllegalStateException("More than $MAX_CHANGES_PER_TICK node network changes occurred in one tick. What the hell are you doing?")
        perTickChangeCount ++
    }

    internal var perTickChangeCount = 0

    companion object {
        internal val MAX_CHANGES_PER_TICK = 100000
    }
}
