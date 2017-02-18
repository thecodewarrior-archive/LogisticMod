package thecodewarrior.logistic.capability

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.teamwizardry.librarianlib.common.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.common.base.capability.ICapabilityObjectProvider
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.network.sendToAllAround
import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.saving.Savable
import com.teamwizardry.librarianlib.common.util.saving.SavableConstructorOrder
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodGetter
import com.teamwizardry.librarianlib.common.util.saving.SaveMethodSetter
import com.teamwizardry.librarianlib.common.util.toRl
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import thecodewarrior.logistic.util.AStar
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class CapabilityLogisticWorld(val world: World) : CapabilityMod("logistic:logisticWorld".toRl()) {

    var tempBlah: UUID? = null

    @get:SaveMethodGetter("graph")
    @set:SaveMethodSetter("graph")
    var graph = SimpleWeightedGraph<LogisticNode, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        get() = field
        private set(value) {
            if(value == null)
                return
            field = value
            byID.clear()
            byOrigin.clear()
            byChunk.clear()

            field.vertexSet().forEach {
                byID.put(it.uuid, it)
                byOrigin.put(it.origin, it)
                byChunk.put(ChunkCoord(it.origin), it)
            }
        }
    val byID = mutableMapOf<UUID, LogisticNode>()
    val byOrigin: Multimap<BlockPos, LogisticNode> = HashMultimap.create()
    val byChunk: Multimap<ChunkCoord, LogisticNode> = HashMultimap.create()

    val nodeSet: Set<LogisticNode>
        get() = graph.vertexSet()
    val edgeSet: Set<DefaultWeightedEdge>
        get() = graph.edgeSet()

    fun getNode(id: UUID): LogisticNode? {
        return byID[id]
    }

    fun getNodes(origin: BlockPos): Collection<LogisticNode> {
        return byOrigin[origin] ?: listOf()
    }

    fun getNodes(chunk: ChunkCoord): Collection<LogisticNode> {
        return byChunk[chunk] ?: listOf()
    }

    fun createNode(pos: Vec3d, origin: BlockPos = BlockPos(0, 0, 0)): LogisticNode {
        var id = UUID.randomUUID()
        while(id in byID)
            id = UUID.randomUUID()

        val node = LogisticNode(origin, pos, id)
        byOrigin.put(node.origin, node)
        byID.put(node.uuid, node)
        byChunk.put(ChunkCoord(node.pos), node)
        graph.addVertex(node)

        PacketHandler.NETWORK.sendToAllAround(PacketAddNodes().apply { ids = arrayOf(id); data = arrayOf(BareLogisticNode(pos, NodeType.NAV))}, world, pos, 128)

        return node
    }

    fun removeNode(node: LogisticNode) {
        graph.removeVertex(node)
        byID.remove(node.uuid)
        byOrigin.remove(node.origin, node)
        byChunk.remove(ChunkCoord(node.pos), node)

        PacketHandler.NETWORK.sendToAllAround(PacketRemoveNodes().apply { ids = arrayOf(node.uuid) }, world, node.pos, 128)
    }

    fun removeAll(origin: BlockPos) {
        // toMutableList 'cause that creates a copy. Don't want any CME's up in my business
        getNodes(origin).toMutableList().forEach { removeNode(it) }
    }

    fun removeAll(chunk: ChunkCoord) {
        // toMutableList 'cause that creates a copy. Don't want any CME's up in my business
        getNodes(chunk).toMutableList().forEach { removeNode(it) }
    }

    fun neighbors(node: LogisticNode): List<LogisticNode> {
        return graph.edgesOf(node).map { if(graph.getEdgeSource(it) == node) graph.getEdgeTarget(it) else graph.getEdgeSource(it) }
    }

    fun getInAABB(aabb: AxisAlignedBB): List<LogisticNode> {

        val minChunk = ChunkCoord(Vec3d(aabb.minX, aabb.minY, aabb.minZ))
        val maxChunk = ChunkCoord(Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ))

        val list = mutableListOf<LogisticNode>()

        for(x in minChunk.chunkX..maxChunk.chunkX) {
            for(z in minChunk.chunkZ..maxChunk.chunkZ) {

                list.addAll(getNodes(ChunkCoord(x, z)).filter {
                    it.pos.xCoord <= aabb.maxX && it.pos.xCoord >= aabb.minX &&
                            it.pos.yCoord <= aabb.maxY && it.pos.yCoord >= aabb.minY &&
                            it.pos.zCoord <= aabb.maxZ && it.pos.zCoord >= aabb.minZ
                })

            }
        }

        return list
    }

    fun path(from: UUID, to: UUID) {
        val fromNode = getNode(from)
        val toNode = getNode(to)
        if(fromNode == null || toNode == null)
            return
        val path = AStar.pathfind(fromNode, toNode, graph, { (it.pos - toNode.pos).lengthSquared() }, { it * it })
        if(path.isEmpty()) {
            (world as? WorldServer)?.spawnParticle(EnumParticleTypes.SMOKE_LARGE, fromNode.pos.xCoord, fromNode.pos.yCoord, fromNode.pos.zCoord, 1, 0.0, 0.0, 0.0, 0.0)
            (world as? WorldServer)?.spawnParticle(EnumParticleTypes.SMOKE_LARGE, toNode.pos.xCoord, toNode.pos.yCoord, toNode.pos.zCoord, 1, 0.0, 0.0, 0.0, 0.0)
        } else {
            path.forEach { node ->
                (world as? WorldServer)?.spawnParticle(EnumParticleTypes.BARRIER, node.pos.xCoord, node.pos.yCoord, node.pos.zCoord, 1, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }

    companion object {
        @JvmStatic
        @CapabilityInject(CapabilityLogisticWorld::class)
        lateinit var cap: Capability<CapabilityLogisticWorld>

        fun init() {
            println("Init")
            register(CapabilityLogisticWorld::class.java, ICapabilityObjectProvider {
                cap
            })
            MinecraftForge.EVENT_BUS.register(CapabilityLogisticWorld::class.java)
        }
    }

    fun connect(a: LogisticNode, b: LogisticNode) {
        if(a == b)
            return

        var blocked = false

        val result = world.rayTraceBlocks(a.pos, b.pos)

        if(result != null && result.typeOfHit != RayTraceResult.Type.MISS) {
            blocked = true
        }

        if(blocked)
            return

        graph.addEdge(a, b)
        graph.setEdgeWeight(graph.getEdge(a, b), (a.pos - b.pos).lengthVector())

        PacketHandler.NETWORK.sendToAllAround(PacketAddEdges().apply { idsFrom = arrayOf(a.uuid); idsTo = arrayOf(b.uuid) }, world, a.pos, 128)
    }

    fun areConnected(a: LogisticNode, b: LogisticNode): Boolean {
        return graph.containsEdge(a, b)
    }
}

data class ChunkCoord(val chunkX: Int, val chunkZ: Int) {
    constructor(pos: BlockPos) : this(pos.x/16, pos.z/16)
    constructor(pos: Vec3d) : this(pos.xCoord.toInt()/16, pos.zCoord.toInt()/16)
}

@Savable
open class LogisticNode
@SavableConstructorOrder("origin", "pos", "uuid")
    constructor(
            val origin: BlockPos,
            val pos: Vec3d,
            val uuid: UUID
    ) {
}

enum class NodeType {
    NAV
}
