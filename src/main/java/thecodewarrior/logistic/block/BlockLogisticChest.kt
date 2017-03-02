package thecodewarrior.logistic.block

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import thecodewarrior.logistic.api.LogisticAPI

/**
 * Created by TheCodeWarrior
 */
class BlockLogisticChest : BlockMod("logisticChest", Material.ROCK){
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        BlockPos.getAllInBox(pos, pos.add(100, 5, 100)).forEach {
            LogisticAPI.forWorld(worldIn).addNode(it, vec(0.5, 1.5, 0.5), null)
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)

        BlockPos.getAllInBox(pos, pos.add(100, 5, 100)).forEach {
            LogisticAPI.forWorld(worldIn).removeNode(pos)
        }
    }
}
