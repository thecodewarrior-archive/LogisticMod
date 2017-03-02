package thecodewarrior.logistic.logistics.sync

import com.teamwizardry.librarianlib.common.network.PacketHandler
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.WorldServer
import thecodewarrior.logistic.logistics.WorldCapLogistic
import thecodewarrior.logistic.logistics.nodes.Node
import thecodewarrior.logistic.packets.PacketNodes
import java.util.*

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
        val changeTrackers = arrayOf(nodesAdded, nodesDeleted)
        if(changeTrackers.all { it.changes.isEmpty() })
            return
        world.playerEntities.forEach { player ->
            player as EntityPlayerMP
            val deleted = nodesDeleted.changesFor(player)
            val added = nodesAdded.changesFor(player)
            if(added.isNotEmpty() || deleted.isNotEmpty())
                PacketHandler.NETWORK.sendTo(PacketNodes(added, deleted), player)
        }

        changeTrackers.forEach { it.clear() }
    }
}

class ChangeCounter<C> {
    val changes = mutableMapOf<ChunkPos, Deque<C>>()

    fun add(chunk: ChunkPos, change: C) {
        changes.getOrPut(chunk, { LinkedList() }).push(change)
    }

    fun changesFor(player: EntityPlayerMP) : List<C> {
        val chunkMap = player.serverWorld.playerChunkMap

        return changes.filter { chunkMap.isPlayerWatchingChunk(player,  it.key.chunkXPos, it.key.chunkZPos) }
                .values.fold(mutableListOf<C>()) { fold, value -> fold.addAll(value); fold }
    }

    fun clear() {
        changes.values.clear()
    }
}
