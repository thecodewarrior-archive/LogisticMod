package thecodewarrior.logistic.items

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.util.ifCap
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.sendSpamlessMessage
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import thecodewarrior.logistic.logistics.CapabilityLogisticWorld
import thecodewarrior.logistic.logistics.PacketSetPaths
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class ItemNavPoint : ItemMod("navPoint") {

    companion object {
        var tempFooBar: UUID? = null
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        return worldIn.ifCap(CapabilityLogisticWorld.cap, null) { cap ->
            if(player.isSneaking) {
                val list = cap.getNodes(pos.offset(facing))
                if(list.isNotEmpty()) {
                    val first = list.first()

                    val uuid = first.uuid
                    val t = tempFooBar
                    if(t == null)
                        tempFooBar = uuid
                    else {
                        tempFooBar = null
                        val path = cap.path(t, uuid)

                        val drone = cap.getIdleDrone()

                        drone.path = path.toTypedArray()
                        drone.idleAt = null

                        if(player is EntityPlayerMP)
                            PacketHandler.NETWORK.sendToDimension(PacketSetPaths().apply { ids = intArrayOf(drone.id); paths = arrayOf(path.toTypedArray()) }, player.world.provider.dimension)
                    }

                    player.sendSpamlessMessage("INetwork ${first.networkID}", "net")

                }
            } else {
                if (cap.getNodes(pos.offset(facing)).isNotEmpty()) {
                    cap.removeAll(pos.offset(facing))
                } else {
                    val node = cap.createNode(Vec3d(pos.offset(facing)) + vec(0.5, 0.5, 0.5), pos.offset(facing))

                    val radius = 16.0
                    val aabb = AxisAlignedBB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius)

                    cap.getInAABB(aabb).forEach {
                        cap.connect(node, it)
                    }
                }
            }
            return@ifCap EnumActionResult.SUCCESS
        } ?: EnumActionResult.PASS
    }
}
