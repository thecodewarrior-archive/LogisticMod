package thecodewarrior.logistic.capability

import com.teamwizardry.librarianlib.common.util.saving.Savable
import com.teamwizardry.librarianlib.common.util.saving.SavableConstructorOrder
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
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

    fun updateEdgeEndpoints(edge: EdgeData) {
        edge.aPos = nodes[edge.aID]?.pos ?: vec(0,0,0)
        edge.bPos = nodes[edge.bID]?.pos ?: vec(0,0,0)
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
}

@Savable
data class BareLogisticNode
@SavableConstructorOrder("pos", "type") constructor(val pos: Vec3d, val type: NodeType)
