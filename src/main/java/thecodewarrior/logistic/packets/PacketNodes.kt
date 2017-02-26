package thecodewarrior.logistic.packets

import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.plus
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import thecodewarrior.logistic.logistics.nodes.Node

/**
 * Created by TheCodeWarrior
 */
@PacketRegister(Side.CLIENT)
class PacketNodes() : PacketBase() {
    var blocks = arrayOf<BlockPos>()
    var vecs = arrayOf<Vec3d>()

    var removed = arrayOf<BlockPos>()

    constructor(deletions: List<BlockPos>, additions: List<Node>) : this() {
        removed = deletions.toTypedArray()
        blocks = additions.map { it.pos }.toTypedArray()
        vecs = additions.map { it.offset + Vec3d(it.pos) }.toTypedArray()
    }

    override fun handle(ctx: MessageContext) {
    }
}
