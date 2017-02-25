package thecodewarrior.logistic.block

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import thecodewarrior.logistic.api.LogisticAPI
import thecodewarrior.logistic.logistics.NavigationNode

/**
 * Created by TheCodeWarrior
 */
class BlockLogisticChest : BlockMod("logisticChest", Material.ROCK){
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        LogisticAPI.forWorld(worldIn).addNode(pos, vec(0.5, 1.5, 0.5), NavigationNode)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)

        LogisticAPI.forWorld(worldIn).removeNode(pos)
    }
}
