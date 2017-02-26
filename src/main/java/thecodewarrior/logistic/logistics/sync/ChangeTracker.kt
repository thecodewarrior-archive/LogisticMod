package thecodewarrior.logistic.logistics.sync

import com.teamwizardry.librarianlib.common.network.PacketHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
import thecodewarrior.logistic.logistics.WorldCapLogistic
import thecodewarrior.logistic.logistics.nodes.Node
import thecodewarrior.logistic.packets.PacketNodes

/**
 * Created by TheCodeWarrior
 */
class ChangeTracker(val cap: WorldCapLogistic) {

    val nodesAdded = ChangeCounter<Node>()
    val nodesDeleted = ChangeCounter<BlockPos>()

    fun addNode(node: Node) {
        val chunk = ChunkPos(node.pos)
        nodesAdded.add(chunk, node)
    }

    fun removeNode(pos: BlockPos) {
        val chunk = ChunkPos(pos)
        nodesDeleted.add(chunk, pos)
    }

    fun sync(world: WorldServer) {
        world.playerEntities.forEach { player ->
            player as EntityPlayerMP

            PacketHandler.NETWORK.sendTo(PacketNodes(nodesDeleted.changesFor(player), nodesAdded.changesFor(player)), player)
        }
    }
}

class ChangeCounter<C> {
    val changes = mutableMapOf<ChunkPos, MutableList<C>>()

    fun add(chunk: ChunkPos, change: C) {
        changes.getOrPut(chunk, { mutableListOf() }).add(change)
    }

    fun changesFor(player: EntityPlayerMP) : List<C> {
        val chunkMap = player.serverWorld.playerChunkMap

        return changes.filter { chunkMap.isPlayerWatchingChunk(player,  it.key.chunkXPos, it.key.chunkZPos) }
                .values.fold(mutableListOf<C>()) { fold, value -> fold.addAll(value); fold }
    }
}
