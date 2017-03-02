package thecodewarrior.logistic.client

import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thecodewarrior.logistic.util.collections.BlockMap

/**
 * Created by TheCodeWarrior
 */
object ClientNetInfoTracker {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    val nodes = BlockMap<Vec3d>()

    @SubscribeEvent
    fun switchWorld(e: WorldEvent.Unload) {
        nodes.clear()
        NodeNetRenderer.rebuild()
    }
}
