package thecodewarrior.logistic.logistics.nodes

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import thecodewarrior.logistic.logistics.NodeType
import thecodewarrior.logistic.util.collections.BlockFloatMap

/**
 * Created by TheCodeWarrior
 */
class Node(val pos: BlockPos, val offset: Vec3d, val type: NodeType) {
    private val connections = BlockFloatMap() // connections
}
