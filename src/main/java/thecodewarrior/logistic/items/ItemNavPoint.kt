package thecodewarrior.logistic.items

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.util.ifCap
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import thecodewarrior.logistic.capability.CapabilityLogisticWorld

/**
 * Created by TheCodeWarrior
 */
class ItemNavPoint : ItemMod("navPoint") {
    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        return worldIn.ifCap(CapabilityLogisticWorld.cap, null) { cap ->
            if(player.isSneaking) {
                val list = cap.getNodes(pos.offset(facing))
                if(list.isNotEmpty()) {
                    val uuid = list.first().uuid

                    if (cap.tempBlah == null)
                        cap.tempBlah = uuid
                    else {
                        cap.path(cap.tempBlah!!, uuid)
                        cap.tempBlah = null
                    }
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
