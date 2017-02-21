package thecodewarrior.logistic.block

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.util.ifCap
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import thecodewarrior.logistic.logistics.CapabilityLogisticWorld

/**
 * Created by TheCodeWarrior
 */
class BlockLogisticChest : BlockMod("logisticChest", Material.ROCK){
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        worldIn.ifCap(CapabilityLogisticWorld.cap, null) { cap ->
            val node = cap.createNode(Vec3d(pos) + vec(0.5, 1.5, 0.5), pos)

            val radius = 16.0
            val aabb = AxisAlignedBB(pos.x - radius, pos.y - radius, pos.z - radius, pos.x + radius, pos.y + radius, pos.z + radius)

            cap.getInAABB(aabb).forEach {
                cap.connect(node, it)
            }
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)

        worldIn.ifCap(CapabilityLogisticWorld.cap, null) {
            it.removeAll(pos)
        }

    }
}
