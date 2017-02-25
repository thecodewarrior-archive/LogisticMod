package thecodewarrior.logistic.items

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.util.sendSpamlessMessage
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import thecodewarrior.logistic.api.LogisticAPI

/**
 * Created by TheCodeWarrior
 */
class ItemNavPoint : ItemMod("navPoint") {

    companion object {
        var tempFooBar: Int = 0
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val exist = LogisticAPI.forWorld(worldIn).getNode(pos) != null
        player.sendSpamlessMessage("$exist ${tempFooBar++}", "nodeExist")
        return EnumActionResult.SUCCESS
    }
}
