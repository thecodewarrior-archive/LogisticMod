package thecodewarrior.logistic.logistics

import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.saving.Savable
import com.teamwizardry.librarianlib.common.util.saving.SavableConstructorOrder
import com.teamwizardry.librarianlib.common.util.times
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

/**
 * Created by TheCodeWarrior
 */
object ClientLogisticTracker {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    val nodes = mutableMapOf<UUID, BareLogisticNode>()
    val edges = mutableSetOf<EdgeData>()
    val drones = mutableMapOf<Int, DroneData>()

    fun updateEdgeEndpoints(edge: EdgeData) {
        edge.aPos = nodes[edge.aID]?.pos ?: vec(0,0,0)
        edge.bPos = nodes[edge.bID]?.pos ?: vec(0,0,0)
        edge.length = (edge.aPos - edge.bPos).lengthVector()
    }

    fun createDrone(id: Int) {
        drones.put(id, DroneData(id))
    }

    fun setDronePath(id: Int, path: Array<UUID>) {
        if(id !in drones)
            createDrone(id)
        val drone = drones.get(id)!!
        drone.path = path
        drone.len = 0.0
        drone.progress = 0.0
        drone.idleAt = null
    }

    @SubscribeEvent
    fun clientTick(e: TickEvent.ClientTickEvent) {
        drones.forEach {
            val (i, drone) = it
            if(drone.idleAt == null) {
                drone.progress += 1.0/20.0
                if (drone.progress >= drone.len) {
                    drone.progress -= drone.len
                    drone.nextIndex++
                    if(drone.nextIndex < drone.path.size) {
                        drone.posA = nodes[drone.path[drone.nextIndex-1]]!!.pos
                        drone.posB = nodes[drone.path[drone.nextIndex]]!!.pos
                        drone.len = (drone.posA - drone.posB).lengthVector()
                    }
                }
                if (drone.nextIndex >= drone.path.size) {
                    drone.idleAt = nodes[drone.path.last()]?.pos
                    drone.path = arrayOf()
                    drone.nextIndex = 0
                }
            }
        }
    }

    @SubscribeEvent
    fun worldChange(e: WorldEvent.Unload) {
        nodes.clear()
        edges.clear()
    }
}

data class EdgeData(val aID: UUID, val bID: UUID) {
    var aPos: Vec3d = vec(0,0,0)
    var bPos: Vec3d = vec(0,0,0)
    var length: Double = 0.0
}

data class DroneData(val id: Int) {
    var path: Array<UUID> = arrayOf()
    var nextIndex: Int = 0
    var len = 0.0
    var progress = 0.0
    var posA = vec(0,0,0)
    var posB = vec(0,0,0)
    var idleAt: Vec3d? = vec(0,0,0)

    fun currentPos(partialTicks: Float): Vec3d {
        return idleAt ?: posA + (posB - posA) * ((progress + partialTicks/20.0) / len)
    }
}

@Savable
data class BareLogisticNode
@SavableConstructorOrder("pos", "type") constructor(val pos: Vec3d, val type: NodeType)
