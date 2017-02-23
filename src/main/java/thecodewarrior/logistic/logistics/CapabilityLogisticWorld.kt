package thecodewarrior.logistic.logistics

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.teamwizardry.librarianlib.common.base.capability.CapabilityMod
import com.teamwizardry.librarianlib.common.base.capability.ICapabilityObjectProvider
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.network.sendToAllAround
import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.saving.*
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
import thecodewarrior.logistic.api.INetwork
import thecodewarrior.logistic.api.impl.Network
import thecodewarrior.logistic.util.AStar
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class CapabilityLogisticWorld(val world: World) : CapabilityMod("logistic:logisticWorld".toRl()) {

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
    @Save
    val networks = mutableListOf<Network?>()
    val drones = mutableListOf<LogisticDrone?>()

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

        val node = LogisticNode(origin, pos, id, -1)
        byOrigin.put(node.origin, node)
        byID.put(node.uuid, node)
        byChunk.put(ChunkCoord(node.pos), node)
        graph.addVertex(node)

        PacketHandler.NETWORK.sendToAllAround(PacketAddNodes().apply { ids = arrayOf(id); data = arrayOf(BareLogisticNode(pos, NodeType.NAV))}, world, pos, 128)

        return node
    }

    fun removeNode(node: LogisticNode) {
        // toMutableList 'cause that creates a copy. Don't want any CME's up in my business
        graph.edgesOf(node).toMutableList().forEach {
            disconnect(graph.getEdgeSource(it), graph.getEdgeTarget(it))
        }
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

    fun path(from: UUID, to: UUID): List<UUID> {
        val fromNode = getNode(from)
        val toNode = getNode(to)
        if(fromNode == null || toNode == null)
            return listOf()
        if(fromNode.networkID != toNode.networkID || fromNode.networkID == -1 || toNode.networkID == -1)
            return listOf()
        val path = AStar.pathfind(fromNode, toNode, graph, { (it.pos - toNode.pos).lengthSquared() }, { it * it })
        if(path.isEmpty()) {
            (world as? WorldServer)?.spawnParticle(EnumParticleTypes.SMOKE_LARGE, fromNode.pos.xCoord, fromNode.pos.yCoord, fromNode.pos.zCoord, 1, 0.0, 0.0, 0.0, 0.0)
            (world as? WorldServer)?.spawnParticle(EnumParticleTypes.SMOKE_LARGE, toNode.pos.xCoord, toNode.pos.yCoord, toNode.pos.zCoord, 1, 0.0, 0.0, 0.0, 0.0)
        } else {
            path.forEach { node ->
                (world as? WorldServer)?.spawnParticle(EnumParticleTypes.BARRIER, node.pos.xCoord, node.pos.yCoord, node.pos.zCoord, 1, 0.0, 0.0, 0.0, 0.0)
            }
        }
        return path.map { it.uuid }
    }

    fun flood(from: UUID): Set<LogisticNode> {
        val visited = mutableSetOf<LogisticNode>()
        val queued = LinkedList<LogisticNode>()

        val n = getNode(from)!!
        queued.add(n)
        visited.add(n)

        while(queued.isNotEmpty()) { // search algorithm doesn't really matter
            val vert = queued.pop()
            graph.edgesOf(vert).forEach { edge ->
                var other = graph.getEdgeSource(edge)
                if(other == vert)
                    other = graph.getEdgeTarget(edge)

                if(other !in visited) {
                    queued.add(other)
                    visited.add(other)
                }
            }
        }

        return visited
    }

    companion object {
        @JvmStatic
        @CapabilityInject(CapabilityLogisticWorld::class)
        lateinit var cap: Capability<CapabilityLogisticWorld>
            private set

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

        if(a.networkID == -1 && b.networkID != -1) {
            a.networkID = b.networkID
            getNetwork(b.networkID)?.nodes?.add(a.uuid)
        }

        if(b.networkID == -1 && a.networkID != -1) {
            b.networkID = a.networkID
            getNetwork(a.networkID)?.nodes?.add(b.uuid)
        }

        if(a.networkID == -1 && b.networkID == -1) {
            val net = createNetwork()
            a.networkID = net.id
            b.networkID = net.id
            net.nodes.add(a.uuid)
            net.nodes.add(b.uuid)
        }

        if(a.networkID != -1 && b.networkID != -1 && a.networkID != b.networkID) {
            val aNet = getNetwork(a.networkID)!!
            val bNet = getNetwork(b.networkID)!!

            removeNetwork(a.networkID)
            removeNetwork(b.networkID)

            val net = createNetwork()

            net.nodes.addAll(aNet.nodes)
            net.nodes.addAll(bNet.nodes)

            net.nodes.forEach {
                getNode(it)?.networkID = net.id
            }
        }

        PacketHandler.NETWORK.sendToAllAround(PacketAddEdges().apply { idsFrom = arrayOf(a.uuid); idsTo = arrayOf(b.uuid) }, world, a.pos, 128)
    }

    fun disconnect(a: LogisticNode, b: LogisticNode) {
        if(a == b)
            return

        graph.removeEdge(a, b)

        val p = path(a.uuid, b.uuid)
        if(p.isEmpty()) { // we just separated two networks.

            val aNet = getNetwork(a.networkID)
            if(aNet != null) {
                val bNet = createNetwork()

                val bSide = flood(b.uuid)
                val bUUID = bSide.map { it.uuid }

                aNet.nodes.removeAll(bUUID)
                bNet.nodes.addAll(bUUID)

                bSide.forEach { it.networkID = bNet.id }
            }
        }

        PacketHandler.NETWORK.sendToAllAround(PacketRemoveEdges().apply { idsFrom = arrayOf(a.uuid); idsTo = arrayOf(b.uuid) }, world, a.pos, 128)
    }

    fun areConnected(a: LogisticNode, b: LogisticNode): Boolean {
        return graph.containsEdge(a, b)
    }

    fun createNetwork(): INetwork {
        networks.forEachIndexed { i, it ->
            if(it == null) {
                val net = Network(i)
                networks[i] = net
                return@createNetwork net
            }
        }
        val net = Network(networks.size)
        networks.add(net)
        return net
    }

    fun getNetwork(id: Int): INetwork? {
        return networks.getOrNull(id)
    }

    fun removeNetwork(id: Int) {
        if(id > 0 && id < networks.size)
            networks[id] = null
    }

    fun removeEmptyNetworks() {
        networks.forEachIndexed { i, it ->
            if(it?.nodes?.size == 0)
                removeNetwork(i)
        }
    }

    fun getIdleDrone(): LogisticDrone {
        val idle = drones.find { it?.idle ?: false }
        return idle ?: createDrone()
    }

    fun createDrone(): LogisticDrone {
        networks.forEachIndexed { i, it ->
            if(it == null) {
                val drone = LogisticDrone(i)
                drones[i] = drone
                return@createDrone drone
            }
        }
        val drone = LogisticDrone(drones.size)
        drones.add(drone)

        PacketHandler.NETWORK.sendToDimension(PacketAddDrones().apply { ids = intArrayOf(drone.id) }, world.provider.dimension)

        return drone
    }

    fun getDrone(id: Int): LogisticDrone? {
        return drones.getOrNull(id)
    }

    fun removeDrone(id: Int) {
        if(id > 0 && id < drones.size)
            drones[id] = null
    }

    fun updateDrone(id: Int) {
        getDrone(id)?.let { drone ->
            if(!drone.idle) {
                if(drone.nextIndex == -1) {
                    drone.nextIndex = 1
                    drone.len = graph.getEdgeWeight(graph.getEdge(getNode(drone.path[0]), getNode(drone.path[1])))
                }
                drone.progress += drone.speed
                if (drone.progress >= drone.len) {
                    drone.progress -= drone.len
                    drone.nextIndex++
                }
                if (drone.nextIndex >= drone.path.size) {
                    drone.idleAt = drone.path.last()
                    drone.path = arrayOf()
                    drone.nextIndex = 0
                }
            }
//            drone.power--
        }
    }

    fun tick() {
        drones.forEach {
            if(it != null) updateDrone(it.id)
        }
    }
}

data class ChunkCoord(val chunkX: Int, val chunkZ: Int) {
    constructor(pos: BlockPos) : this(pos.x/16, pos.z/16)
    constructor(pos: Vec3d) : this(pos.xCoord.toInt()/16, pos.zCoord.toInt()/16)
}

@Savable
class LogisticNode
@SavableConstructorOrder("origin", "pos", "uuid", "networkID")
    constructor(
            val origin: BlockPos,
            val pos: Vec3d,
            val uuid: UUID,
            var networkID: Int
    ) {
}

@Savable
class LogisticDrone(val id: Int) {
    var idleAt: UUID? = null
    val idle: Boolean
        get() = idleAt != null
    var path: Array<UUID> = arrayOf()
        set(value) {
            field = value
            len = -1.0
            progress = 0.0
            nextIndex = -1
            idleAt = null
        }
    var nextIndex = 0
    var len = 0.0
    var progress = 0.0
    val speed = 1.0/20.0
}

enum class NodeType {
    NAV
}
