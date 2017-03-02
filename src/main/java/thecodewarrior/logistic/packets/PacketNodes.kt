package thecodewarrior.logistic.packets

import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import thecodewarrior.logistic.client.ClientNetInfoTracker
import thecodewarrior.logistic.client.NodeNetRenderer
import thecodewarrior.logistic.logistics.nodes.Node

/**
 * Created by TheCodeWarrior
 */
@PacketRegister(Side.CLIENT)
class PacketNodes() : PacketBase() {
    @Save var added = arrayOf<BlockPos>()
    @Save var vecs = arrayOf<Vec3d>()

    @Save var removed = arrayOf<BlockPos>()

    constructor(additions: Collection<Node>, deletions: Collection<BlockPos>) : this() {
        removed = deletions.toTypedArray()
        added = additions.map { it.pos }.toTypedArray()
        vecs = additions.map { it.offset + Vec3d(it.pos) }.toTypedArray()
    }

    override fun handle(ctx: MessageContext) {
        removed.forEach {
            ClientNetInfoTracker.nodes.remove(it)
        }
        added.indices.forEach {
            ClientNetInfoTracker.nodes.set(added[it], vecs[it])
        }
        NodeNetRenderer.rebuild()
    }
}
